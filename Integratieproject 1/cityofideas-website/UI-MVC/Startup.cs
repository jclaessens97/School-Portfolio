using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Http;
using COI.UI_MVC.Services.MQTT;
using COI.BL;
using COI.BL.Domain.User;
using COI.BL.Impl;
using COI.DAL;
using COI.DAL.Impl;
using COI.DAL.EF;
using COI.UI_MVC.Areas.Identity;
using COI.UI_MVC.Areas.Identity.Services;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.VisualStudio.Web.CodeGeneration.Contracts.ProjectModel;
using COI.UI_MVC.Services;
using COI.UI_MVC.Services.Impl;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;
using COI.UI_MVC.Hubs;
using COI.UI_MVC.Hubs.Impl;
using COI.UI_MVC.Models.Users;
using COI.UI_MVC.Util;
using COI.BL.Algorithms;
using UI_MVC.Scheduler.Scheduling;
using COI.UI_MVC.Scheduler.Tasks;
using System.Diagnostics;
using COI.BL.Application;
using Microsoft.AspNetCore.Authentication;

namespace COI.UI_MVC
{
    public class Startup
    {
        public IConfiguration Configuration { get; }
        public IHostingEnvironment HostingEnvironment { get; }

        public Startup(IConfiguration configuration, IHostingEnvironment hostingEnvironment)
        {
            Configuration = configuration;
            HostingEnvironment = hostingEnvironment;
        }

        private void SetupCookiePolicy(IServiceCollection services)
        {
            services.Configure<CookiePolicyOptions>(options =>
            {
                // This lambda determines whether user consent for non-essential cookies is needed for a given request.
                options.CheckConsentNeeded = context => true;
                options.MinimumSameSitePolicy = SameSiteMode.None;
            });
        }

        private void SetupDbContexts(IServiceCollection services)
        {
            services.AddDbContext<CityOfIdeasDbContext>(options =>
                options.UseMySql(Configuration["Database:ConnectionStrings:DefaultConnection"])
            );
        }

        private void SetupIdentity(IServiceCollection services)
        {
            services
                .AddDefaultIdentity<User>(config => {
                    config.SignIn.RequireConfirmedEmail = true;
                })
                .AddEntityFrameworkStores<CityOfIdeasDbContext>()
                .AddDefaultTokenProviders()
                .AddErrorDescriber<CustomIdentityErrorDescriber>();
            
            services.Configure<AuthMessageSenderOptions>(Configuration);
            services.Configure<IdentityOptions>(options =>
            {
                // Password settings.
                options.Password.RequireDigit = true;
                options.Password.RequireLowercase = true;
                options.Password.RequireNonAlphanumeric = false;
                options.Password.RequireUppercase = true;
                options.Password.RequiredLength = 6;
                options.Password.RequiredUniqueChars = 1;

                // Lockout settings.
                options.Lockout.DefaultLockoutTimeSpan = TimeSpan.FromMinutes(5);
                options.Lockout.MaxFailedAccessAttempts = 5;
                options.Lockout.AllowedForNewUsers = true;

                // User settings.
                options.User.AllowedUserNameCharacters =
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._@+";
                options.User.RequireUniqueEmail = true;
            });
            
            services.AddAuthentication()
                .AddCookie(o =>
                {
                    o.AccessDeniedPath = new PathString("/Identity/Account/Login/");
                    o.LoginPath = new PathString("/Identity/Account/Login/");
                })
                .AddJwtBearer(JwtBearerDefaults.AuthenticationScheme, o =>
                {
                    o.SaveToken = true;
                    o.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidateAudience = false,
                        ValidateIssuer = false,
                        ValidateIssuerSigningKey = true,
//                        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("zevenhonderzwevendebeestenzwermeninhetrond")),
                        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(Configuration["Authentication:Jwt:Bytes"])),
                        ValidateLifetime = true,
                        ClockSkew = TimeSpan.FromMinutes(5)
                    };
                })
                .AddFacebook(facebookOptions =>
                {
                    facebookOptions.AppId = Configuration["Authentication:Facebook:AppId"];
                    facebookOptions.AppSecret = Configuration["Authentication:Facebook:AppSecret"];
                })
                .AddGoogle(googleOptions =>
                {
                    googleOptions.ClientId = Configuration["Authentication:Google:ClientId"];
                    googleOptions.ClientSecret = Configuration["Authentication:Google:ClientSecret"];
                })
                .AddMicrosoftAccount(microsoftOptions =>
                {
                    microsoftOptions.ClientId = Configuration["Authentication:Microsoft:ApplicationId"];
                    microsoftOptions.ClientSecret = Configuration["Authentication:Microsoft:Password"];
                })
                .AddTwitter(twitterOptions =>
                {
                    twitterOptions.ConsumerKey = Configuration["Authentication:Twitter:ConsumerKey"];
                    twitterOptions.ConsumerSecret = Configuration["Authentication:Twitter:ConsumerSecret"];
                    twitterOptions.RetrieveUserDetails = true;
                    twitterOptions.ClaimActions.MapJsonKey("display-name", "name");
                });
            
            services.AddAuthorization(options =>
            {
                options.AddPolicy(
                    Enum.GetName(typeof(Policy), Policy.SuperAdmins), 
                    policy => policy
                        .RequireAssertion(context => context.User.HasClaim(c => c.Type == "SuperAdmin"))
                );

                options.AddPolicy(
                    Enum.GetName(typeof(Policy), Policy.Admins),
                    policy => policy
                        .RequireAssertion(context => context.User.HasClaim(c => c.Type == "Admin" || c.Type == "SuperAdmin"))
                );

                options.AddPolicy(
                    Enum.GetName(typeof(Policy), Policy.Moderators),
                    policy => policy
                        .RequireAssertion(context => context.User.HasClaim(c => c.Type == "Moderator" || c.Type == "Admin" || c.Type == "SuperAdmin"))
                );

                // This is the lowest policy & implies the "SignedIn" policy
                options.AddPolicy(
                    Enum.GetName(typeof(Policy), Policy.Users),
                    policy => policy.RequireAuthenticatedUser()
                );
            });
        }

        private void ConfigureHttp(IServiceCollection services)
        {
            services.AddSubdomains();
            services.AddMvc()
                .AddXmlSerializerFormatters()
                .SetCompatibilityVersion(CompatibilityVersion.Version_2_1)
                .AddJsonOptions(options => 
                    options
                    .SerializerSettings
                    .ReferenceLoopHandling = ReferenceLoopHandling.Ignore);
        }

        private void SetupDependencyInjection(IServiceCollection services)
        {
            // Identity
            services.AddTransient<IEmailSender, EmailSender>();
            services.AddScoped<AuthRepository>();

            // Unit of work
            services.AddScoped<UnitOfWork, UnitOfWork>();
            services.AddScoped<UnitOfWorkManager, UnitOfWorkManager>();

            // Repositories
            services.AddScoped<IIdeationRepository, IdeationRepository>();
            services.AddScoped<IPlatformRepository, PlatformRepository>();
            services.AddScoped<IProjectRepository, ProjectRepository>();
            services.AddScoped<IFormRepository, FormRepository>();
            services.AddScoped<IVerifyRequestRepository, VerifyRequestRepository>();
            services.AddScoped<IPlatformRequestRepository, PlatformRequestRepository>();
            services.AddScoped<IIoTRepository,IoTRepository>();
            services.AddScoped<IActivityRepository, ActivityRepository>();

            // Managers
            services.AddScoped<IIdeationManager, IdeationManager>();
            services.AddScoped<IFormManager, FormManager>();
            services.AddScoped<IPlatformManager, PlatformManager>();
            services.AddScoped<IProjectManager, ProjectManager>();
            services.AddScoped<IVerifyRequestManager, VerifyRequestManager>();
            services.AddScoped<IPlatformRequestManager, PlatformRequestManager>();
            services.AddScoped<ITrendingAlgorithm, TrendingAlgorithm>();
            services.AddScoped<IIoTManager, IoTManager>();
            services.AddScoped<IActivityManager, ActivityManager>();
            
            // Application Controllers
            services.AddScoped<IVoteController, VoteController>();

            // UI Services
            services.AddScoped<MqttBroker, MqttBroker>();
            if (HostingEnvironment.IsDevelopment())
            {
                services.AddScoped<IFileUploader, LocalFileUploader>();
            }
            else
            {
                services.AddScoped<IFileUploader, GoogleStorageUploader>();
            }

            // Scheduler
            services.AddSingleton<IScheduledTask, VoteSpreadTask>();
            services.AddScheduler((sender, args) =>
            {
                Console.Write(args.Exception.Message);
                args.SetObserved();
            });
        }

        private void SetupSignalR(IServiceCollection services)
        {
            services.AddSignalR();
        }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            SetupCookiePolicy(services);
            SetupDbContexts(services);
            SetupIdentity(services);
            ConfigureHttp(services);
            SetupDependencyInjection(services);
            SetupSignalR(services);
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(
            IApplicationBuilder app, 
            IHostingEnvironment env, 
            IApplicationLifetime applicationLifetime 
        )
        {
            // Start MQTT
           var broker = new MqttBroker(Configuration, app.ApplicationServices.GetService<IServiceProvider>());

            // On application exit terminate MQTT to make sure the connection is ended properly
           applicationLifetime.ApplicationStopping.Register(() => broker.Terminate());

            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
                app.UseDatabaseErrorPage();
            }
            else
            {
                app.UseExceptionHandler("/Home/Error");
                app.UseHsts();
                app.UseHttpsRedirection(); //enkel redirecten in production aangezien https niet werkt in development
            }

            app.UseAuthentication();
            app.UseStaticFiles();
            app.UseCookiePolicy();

            app.UseMvc(routes =>
            {
                routes.MapSubdomainRoute(
                    Constants.hostnames,
                    "TenantInSubdomain",
                    "{tenant}",
                    "{controller}/{action}",
                    new { controller = "Platform", action = "Index" }
                );

                routes.MapRoute(
                    name: "default",
                    template: "{controller=Home}/{action=Index}/{id?}");
            });

            app.UseSignalR(routes =>
            {
                routes.MapHub<VoteHub>("/votehub");
                routes.MapHub<ActivityHub>("/activityhub");
            });
        }
    }
}