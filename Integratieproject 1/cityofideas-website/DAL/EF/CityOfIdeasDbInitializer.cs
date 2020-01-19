using COI.BL.Domain.Activity;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Platform;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore.Metadata.Conventions.Internal;

namespace COI.DAL.EF
{
    internal static class CityOfIdeasDbInitializer
    {
        private static bool hasRunDuringAppExecution = false;

        public static void Initialize(CityOfIdeasDbContext context, bool dropCreateDatabase = false)
        {
            if (!hasRunDuringAppExecution)
            {
                // Delete database if requested
                if (dropCreateDatabase)
                    context.Database.EnsureDeleted();

                // Create database and seed dummy-data if needed 
                if (context.Database.EnsureCreated()) // 'false' if database already exists
                    // Seed initial (dummy-)data into newly created database
                    Seed(context);

                hasRunDuringAppExecution = true;
            }
        }

        private static void Seed(CityOfIdeasDbContext ctx)
        {
            #region platform

            // platforms
            var m1 = new Media()
            {
                Name = "havenbedrijf logo",
                Url = "https://00e9e64bacc56e2eda613eee13391b31d05177a58d4a489656-apidata.googleusercontent.com/download/storage/v1/b/ui-gen/o/platform-logos%2F1559949616805_Havenbedrijf?qk=AD5uMEs9XBeMlp77tXwRyUqjPOolJPiVMi3Z6Ks0-AiXmrTs_-1JMyWXlS7Assr1WNduzzxJUqsxoQnyNpsQve0ucYSBGyGA5qowQYPe_sLXquh3c1hRoBF52W7yyXoFLUMwnF8-r67-dTALcv9I7jPTzWXQSSbMzuzRnnFVitHPH33EJmOXe3SMJ6uAKIH9BYfyuYuwRQ7HF6ix4sSrTgCoL5NvhVgoDxroV1_EvsLoZB9_goWHXSrMLCor77F-RLrkew3JqJUowDZykZ6LtsjamiFP9nz5O0dX6lSRyoOScUNRBPbBBbZbgpEL64vEMU9LrhimUAM7wYckVew6LQuGAJYLrhqTwPjnaCerLttjdLEomuTc0Co6lG6bxjBLBsXNhv4kTpKjvt-TlVyosof0frSX0o0-i2ToXAgmEzMMcNe29Q71KYjQWJjNDOwaRMgAXCF9LZktm-XQ9mwaMf2_8-k4ACkYpkBTzLUm6wGeVZwlVqjBdPwVSNpVuKgDSddiP1mMORmpOwRggmm490OVeMXBNa0NUmQKGRxW2_lMzcIzWVsKr5SMkVCkLKqDZGQIb_9nOYpzovAAOIzGxjQXpE1iC-DsQJhHkZCtUwR8L3YxFHPNZeR07VUMNV2XN3X-ixszex5_4nCGGBzwXd84IrLKdDyCgWlyLkfUiKl3qOARlUeAiYPvTNL1Xx2yg0K2C-Lf7z5GcarJkDmvMO5b8GyjnWtrt369j2FWHHXT8bHggnG6Z6SEk9ni_tsE9oIGpUqniqDBp_sxZvgMZ44EXM3kD3QOYfp3i0qFgfG2xuS4ZKrf6to"
            };
            ctx.MediaFiles.Add(m1);

            var m1B = new Media()
            {
                Name = "Havenbedrijf banner",
                Url = "https://www.smartbiz.be/wp-content/uploads/sites/5/2018/06/portofantwerp.jpg",
            };

            ctx.MediaFiles.Add(m1B);

            var plat1 = new Platform()
            {
                Name = "Havenbedrijf",
                Tenant = "havenbedrijf2",
                Description =
                    "De 1.584 medewerkers van het Havenbedrijf Antwerpen staan in voor een vlotte en veilige dagelijkse werking van de haven. Gezien het omvangrijke takenpakket variëren de beroepen van al deze collega’s sterk: van sluiswachter tot boekhoudkundige, van toezichter tot promotiemedewerker en van sleepbootkapitein tot kraanmachinist. Samen werken zij aan een duurzame toekomst van de Antwerpse haven.",
                Logo = m1,
                Projects = new List<Project>(),
                Banner = m1B
            };

            var stylesheet1 = new ColorScheme()
            {
                SocialBarColor = "#5D5C61",
                NavBarColor = "#FFFFFF",
                BannerColor = "#7395AE",
                ButtonColor = "#e4032c",
                ButtonTextColor = "#FFFFFF",
                TextColor = "#000000",
                BodyColor = "#FFFFFF"
            };
            plat1.ColorScheme = stylesheet1;
            ctx.Platforms.Add(plat1);

            var m2 = new Media()
            {
                Name = "Stad Antwerpen logo",
                Url = "https://www.antwerpen.be/assets/aOS/gfx/gui/a-logo.png"
            };
            
            var m2B = new Media()
            {
                Name = "Stad Antwerpen banner",
                Url = "https://www.buurtaandestroom.be/wp-content/uploads/2017/06/8juni2.jpg",
            };

            var plat2 = new Platform()
            {
                Name = "Stad Antwerpen",
                Tenant = "stadantwerpen2",
                Description =
                    "Antwerpen is een havenstad aan de Belgische rivier de Schelde die zijn oorsprong vindt in de middeleeuwen. In het centrum bevindt zich het eeuwenoude Diamond Disctrict huis waar er duizenden diamant-handelaars, -slijpers en -snijders. Op de Grote Markt bevindt zich het historische centrum van de stad. In het 17de eeuwse Rubenshuis worden er in kamers werken van de vlaamse Barok schilder Peter Paul Rubens tentoon gesteld. Verder heeft Antwerpen natuurlijk ook zijn grote winkelstraten en veel meer.",
                Logo = m2,
                Projects = new List<Project>(),
                Banner = m2B
            };

            var stylesheet2 = new ColorScheme()
            {
                SocialBarColor = "#373737",
                NavBarColor = "#F4F4F4",
                BannerColor = "#DCD0C0",
                ButtonColor = "#C0B283",
                ButtonTextColor = "#FFFFFF",
                TextColor = "#000000",
                BodyColor = "#FFFFFF"
            };

            plat2.ColorScheme = stylesheet2;
            ctx.Platforms.Add(plat2);

            Media i1 = new Media()
            {
                Name = "De scheldekaaien",
                Url = "https://www.sigmaplan.be/uploads/cache/scheldekaaien-antwerpen-ingekort-1.jpg"
            };

            Media i2 = new Media()
            {
                Name = "De ring",
                Url = "http://buur.be/wp-content/uploads/2018/06/ODR-MAN-004.jpg"
            };

            Media i3 = new Media()
            {
                Name = "Park spoor noord",
                Url =
                    "https://www.agvespa.be/sites/default/files/styles/max_1080p/public/header-images/hr_201409_av_kopspnoord_25.jpg?itok=LpbnfWH8"
            };

            Media i4 = new Media()
            {
                Name = "Turnhoutsebaan",
                Url =
                    "http://www.mediahuisconnect.be/uploads/cache/optimjpg/uploads/media/56cf266151798/Turnhoutsebaan-Borgerhout.jpg"
            };

            #endregion

            #region projects

            Project p1 = new Project()
            {
                Goal =
                    "Over een lengte van meer dan 5 kilometer geven we samen met het Antwerpse stadsbestuur de Scheldekaaien een grondige facelift. Dat is nodig, want de kaaimuur is in slechte staat en de kaaien lopen onder bij hevige stormvloeden. De kaaimuur is niet overal in even slechte staat. Daarom verdeelden we ze onder in zeven zones. In elke zone wordt een specifieke stabilisatietechniek toegepast, toegespitst op de toestand van de kaaimuur. Als kers op de taart transformeert de stad de kaaien in een gezellige plek aan het water.",
                Title = "Het verbouwen van de kaaien",
                CurrentPhaseNumber = 2,
                Logo = i1,
                Phases = new List<Phase>(),
                StartDate = new DateTime(2019, 05, 19),
                EndDate = new DateTime(2020, 09, 10)
            };

            Project p2 = new Project()
            {
                Goal =
                    "Het is eens genoeg geweest met de files op de ring. Het wordt tijd dat we hier iets aan doen. Daarom presenteren we voor jullie project ringland.",
                Title = "Hoe zorgen we voor minder files",
                CurrentPhaseNumber = 2,
                Logo = i2,
                Phases = new List<Phase>(),
                StartDate = new DateTime(2020, 06, 29),
                EndDate = new DateTime(2021, 09, 10)
            };

            Project p3 = new Project()
            {
                Goal = "Tijd dat we hier nog eens werk van maken. Hoe zullen we dit aanpakken?",
                Title = "Het vernieuwen van Park Spoor Noord",
                CurrentPhaseNumber = 2,
                Logo = i3,
                Phases = new List<Phase>(),
                StartDate = new DateTime(2021, 06, 29),
                EndDate = new DateTime(2022, 09, 10)
            };
            
            Project p4 = new Project()
            {
                Goal =
                    "De turnhoutsebaan is een gevaarlijke baan om op te fietsen. We willen deze dan ook heraanleggen om een veilige doorstroom te krijgen naar het stadscentrum. We merken ook op dat autobestuurders op de trambedding rijden wanneer er auto's stilstaan, een fietspasd zou hier dus voor extra overlast kunnen zorgen waardoor het openbaar vervoer gehinderd wordt. We zoeken een oplossing om elk type bestuurder hier vlot door te laten stromen.",
                Title = "Turnhoutsebaan fietsvriendelijker maken",
                CurrentPhaseNumber = 1,
                Logo = i4,
                Phases = new List<Phase>(),
                StartDate = new DateTime(2019, 06, 8),
                EndDate = new DateTime(2020, 04, 1)
            };

            ctx.Projects.Add(p1);
            ctx.Projects.Add(p2);
            ctx.Projects.Add(p3);
            ctx.Projects.Add(p4);
            plat1.Projects.Add(p1);
            plat1.Projects.Add(p2);
            plat1.Projects.Add(p3);
            plat2.Projects.Add(p4);

            //phases
            Phase ph1 = new Phase()
            {
                Description =
                    "Vlak bij het Eilandje en het MAS, aan de Tavernierkaai en Van Meterenkaai, stabiliseren we de kaaimuur. Omdat de site heel wat erfgoed herbergt, worden de werken begeleid door een team van archeologen. Zij kregen de kans om de historische muur uitvoerig te bestuderen.",
                Number = 1,
                Title = "Afbreekfase"
            };

            Phase ph2 = new Phase()
            {
                Description =
                    "In de zones ‘Schipperskwartier en Centrum’ en ‘Bonapartedok en Loodswezen’ van het Antwerpse Scheldekaaienproject zijn de werken volop aan de gang. We stabiliseren er de historische kaaimuur. Grootscheepse werken, die vaak nieuwsgierige blikken van voorbijgangers opwekken. Vanaf vandaag kan iedereen zijn ogen de kost geven vanop twee uitkijkpunten. Wie meer wil weten over het project is bovendien welkom in het infopunt Scheldekaaien, dat een grondige make-over kreeg. ",
                Number = 2,
                Title = "Bouwfase"
            };

            Phase ph3 = new Phase()
            {
                Description =
                    "De Belvédère is een groot, groen, architecturaal zeshoekig uitkijkpunt van 2 hectare aan de Schelde. Op het uitkijkpunt legde het Antwerpse vastgoedbedrijf AG VESPA samen met aannemer Artes Roegiers een park aan met 65 nieuwe bomen, een wandelpad en een plein aan de ingang van de Belvédère. “Het wordt ongetwijfeld een nieuwe trekpleister met veel allure om er te wandelen, uit te waaien, te sporten of om er te genieten van een mooi zicht op de Schelde en onze prachtige stad”, benadrukt burgemeester Bart De Wever. De komende jaren volgt de rest van het Droogdokkenpark: een unieke groene uitwaaiplek van 11 hectare.",
                Number = 3,
                Title = "Afwerkfase"
            };
            ctx.Phases.Add(ph1);
            ctx.Phases.Add(ph3);
            ctx.Phases.Add(ph2);

            p1.Phases.Add(ph1);
            p1.Phases.Add(ph2);
            p1.Phases.Add(ph3);

            Phase ph4 = new Phase()
            {
                Description = "Planningfase",
                Number = 1,
                Title = "Afbreekfase"
            };

            Phase ph5 = new Phase()
            {
                Description = "Afbreekfase",
                Number = 2,
                Title = "Bouwfase"
            };

            Phase ph6 = new Phase()
            {
                Description = "Bouwfase",
                Number = 3,
                Title = "Afwerkfase"
            };
            
            Phase ph7 = new Phase()
            {
                Description = "Plannen voor het ontwerp en project om de turnhoutsebaan veiliger te maken.",
                Number = 1,
                Title = "Planningfase"
            };
            
            Phase ph8 = new Phase()
            {
                Description = "Omleiden van het verkeer langs Plantin Moretuslei.",
                Number = 2,
                Title = "Omleidingsfase"
            };
            
            Phase ph9 = new Phase()
            {
                Description = "We leggen 1 helft aan.",
                Number = 3,
                Title = "Heraanlegfase 1"
            };
            
            Phase ph10 = new Phase()
            {
                Description = "We leggen de andere helft aan.",
                Number = 4,
                Title = "Heraanlegfase 2"
            };
            
            Phase ph11 = new Phase()
            {
                Description = "Testen van de tramlijnen.",
                Number = 5,
                Title = "Doorstroomtest"
            };

            ctx.Phases.Add(ph4);
            ctx.Phases.Add(ph5);
            ctx.Phases.Add(ph6);
            ctx.Phases.Add(ph7);
            ctx.Phases.Add(ph8);
            ctx.Phases.Add(ph9);
            ctx.Phases.Add(ph10);
            ctx.Phases.Add(ph11);

            p2.Phases.Add(ph4);
            p2.Phases.Add(ph5);
            p2.Phases.Add(ph6);
            p4.Phases.Add(ph7);
            p4.Phases.Add(ph8);
            p4.Phases.Add(ph9);
            p4.Phases.Add(ph10);
            p4.Phases.Add(ph11);
            ctx.SaveChanges();

            #endregion

            #region users

            var passwordHash = new PasswordHasher<IdentityUser>();

            User treecompany = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d204b4",
                UserName = "Tree_Company",
                NormalizedUserName = "TREE_COMPANY",
                Email = "sam.geens@student.kdg.be",
                NormalizedEmail = "SAM.GEENS@STUDENT.KDG.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "GUOEBGUORNGINROIUZGBINGOUJRZNLGKNROUGNORZILNGOUNGRO",
                FirmName = "Tree Company"
            };

            User havenbedrijf = new User()
            {
                Id = "0727b106-9e17-49d9-8a35-1d603801e720",
                UserName = "Havenbedrijf",
                NormalizedUserName = "HAVENBEDRIJF",
                Email = "brecht.pallemans@gmail.com",
                NormalizedEmail = "BRECHT.PALLEMANS@GMAIL.COM",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "OGINROUNGioJGORINIRGJPIRZNGHOUKiodgozriJgkRBZE",
                FirmName = "Havenbedrijf"
            };

            User stadantwerpen = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d204b6",
                UserName = "Stad_Antwerpen",
                NormalizedUserName = "STAD_ANTWERPEN",
                Email = "jeroen.claessens.1@student.kdg.be",
                NormalizedEmail = "JEROEN.CLAESSENS.1@STUDENT.KDG.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "GBOURBGOINEGOIURZNOUIGNLKRZNGOUIZRNOIGNOURGNOUIRZ",
                FirmName = "Stad Antwerpen"
            };
            
            User josVermeulen = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d984b0",
                UserName = "Jos_Vermeulen",
                NormalizedUserName = "JOS_VERMEULEN",
                Email = "jos.vermeulen@hotmail.be",
                NormalizedEmail = "JOS.VERMEULEN@HOTMAIL.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "GUOEBGUORNGINROIUZABCENGOUJRZNLGKNROUGNORZILNGOUNGRO",
                FirstName = "Jos",
                LastName = "Vermeulen"
            };

            User arneCools = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d204b5",
                UserName = "Arne_Cools",
                NormalizedUserName = "ARNE_COOLS",
                Email = "arne.cools@student.kdg.be",
                NormalizedEmail = "ARNE.COOLS@STUDENT.KDG.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "UBGROUZBGOURZNGHIONROUIGHOZUIGHOIRZHNGOIRZ",
                FirstName = "Arne",
                LastName = "Cools"
            };
            p1.Moderators.Add(new ProjectModerator()
            {
                Project = p1,
                User = arneCools
            });

            User jensVanOrden = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d204b7",
                UserName = "Jens_VanOrden",
                NormalizedUserName = "JENS_VANORDEN",
                Email = "jens.vanorden@student.kdg.be",
                NormalizedEmail = "jens.vanorden@STUDENT.KDG.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "BGUBEZORUGHORIZHNGOUNGLKNOURGNILRJNGIOZRJNGIONRZ",
                FirstName = "Jens",
                LastName = "VanOrden"
            };
            
            User robbeDumont = new User()
            {
                Id = "ea72c4fc-bbd9-3541-9dcb-0bce45d20489",
                UserName = "Robbe_Dumont",
                NormalizedUserName = "ROBBE_DUMONT",
                Email = "robbe.dumont@student.kdg.be",
                NormalizedEmail = "ROBBE.DUMONT@STUDENT.KDG.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "UBGROUZBGOURZNGHIONROUIGHOZUIGHOIRZHNGOIRZ",
                FirstName = "Robbe",
                LastName = "Dumont"
            };

            User bartDeWever = new User()
            {
                Id = "ea72c4fc-bbd9-3541-9dcb-0bce45d20367",
                UserName = "Bart_Dewever",
                NormalizedUserName = "BART_DEWEVER",
                Email = "bart.dewever@student.kdg.be",
                NormalizedEmail = "BART.DEWEVER@STUDENT.KDG.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "UBGROUZBGOURZNGHIONROUIGHOZUIGHOIRZHNGOIRZ",
                FirstName = "Bart",
                LastName = "De Wever",
            };

            
            User josvermeulenBvba = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d204b9",
                UserName = "JosVermeulen_BVBA",
                NormalizedUserName = "JOSVERMEULEN_BVBA",
                Email = "jv.bvba@jv.be",
                NormalizedEmail = "JV.BVBA@JV.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "OGINROUNGioJGORINIRGJPIRZNGHOUKiodgozriJgkRBZE",
                FirmName = "JosVermeulen BVBA"
            };

            /*User cronos = new User()
            {
                Id = "ea72c4fc-bbd9-4541-9dcb-0bce45d204c1",
                UserName = "Cronos_Benelux",
                NormalizedUserName = "CRONOS_BENELUX",
                Email = "cronos.benelux@cronos.be",
                NormalizedEmail = "CRONOS.BENELUX@CRONOS.BE",
                EmailConfirmed = true,
                PasswordHash = passwordHash.HashPassword(null, "Test-1"),
                SecurityStamp = "ONGIONROUNBGLKQSJIngoeuNLKNGouNRLKNGOUnrzoilkNGOUInOI",
                FirmName = "Cronos Benelux"
            };*/

            ctx.Add(treecompany);
            ctx.Add(havenbedrijf);
            ctx.Add(stadantwerpen);
            ctx.Add(arneCools);
            ctx.Add(jensVanOrden);
            ctx.Add(robbeDumont);
            ctx.Add(bartDeWever);
            ctx.Add(josVermeulen);
            ctx.Add(josvermeulenBvba);
//            ctx.Add(google);
//            ctx.Add(cronos);
            

            /*VerifyRequest verifyRequest = new VerifyRequest()
            {
                Date = DateTime.Now,
                Reason = "Ben al zo lang gebruiker (5 jaar) dat ik geverifieerd wil worden.",
                UserId = bartDeWever.Id,
                UserName = bartDeWever.UserName
            };*/

            VerifyRequest verifyRequest2 = new VerifyRequest()
            {
                Date = DateTime.Now,
                Reason = "Wil heel graag geverifieerd worden.",
                UserId = robbeDumont.Id,
                UserName = robbeDumont.UserName
            };
            
//            ctx.Add(verifyRequest);
            ctx.Add(verifyRequest2);

            //Claims
            IdentityUserClaim<string> superadminClaim = new IdentityUserClaim<string>()
            {
                UserId = treecompany.Id,
                ClaimType = "SuperAdmin",
                ClaimValue = "SuperAdmin"
            };
            
            IdentityUserClaim<string> userClaim3 = new IdentityUserClaim<string>()
            {
                UserId = josVermeulen.Id,
                ClaimType = "stadantwerpen2",
                ClaimValue = "User"
            };
            
            IdentityUserClaim<string> orgJosVermeulen = new IdentityUserClaim<string>()
            {
                UserId = josvermeulenBvba.Id,
                ClaimType = "Organisation",
                ClaimValue = "Organisation"
            };
            
            IdentityUserClaim<string> orgJosVermeulen2 = new IdentityUserClaim<string>()
            {
                UserId = josvermeulenBvba.Id,
                ClaimType = "havenbedrijf2",
                ClaimValue = "User"
            };

            IdentityUserClaim<string> adminClaim = new IdentityUserClaim<string>()
            {
                UserId = havenbedrijf.Id,
                ClaimType = "havenbedrijf2",
                ClaimValue = "Admin"
            };

            IdentityUserClaim<string> admin2Claim = new IdentityUserClaim<string>()
            {
                UserId = stadantwerpen.Id,
                ClaimType = "stadantwerpen2",
                ClaimValue = "Admin"
            };

            IdentityUserClaim<string> modClaim = new IdentityUserClaim<string>()
            {
                UserId = arneCools.Id,
                ClaimType = "havenbedrijf2",
                ClaimValue = "Moderator"
            };
            
            IdentityUserClaim<string> mod2Claim = new IdentityUserClaim<string>()
            {
                UserId = jensVanOrden.Id,
                ClaimType = "stadantwerpen2",
                ClaimValue = "Moderator"
            };

            /*IdentityUserClaim<string> modZClaim = new IdentityUserClaim<string>()
            {
                UserId = bartDeWever.Id,
                ClaimType = "Moderator",
                ClaimValue = "Moderator"
            };*/

            IdentityUserClaim<string> userClaim = new IdentityUserClaim<string>()
            {
                UserId = robbeDumont.Id,
                ClaimType = "havenbedrijf2",
                ClaimValue = "User"
            };
            
            IdentityUserClaim<string> userClaim2 = new IdentityUserClaim<string>()
            {
                UserId = robbeDumont.Id,
                ClaimType = "stadantwerpen2",
                ClaimValue = "User"
            };
            
            /*IdentityUserClaim<string> mod2Claim = new IdentityUserClaim<string>()
            {
                UserId = jeroenClaessens.Id,
                ClaimType = "Moderator",
                ClaimValue = "Moderator"
            };*/

            /*IdentityUserClaim<string> userClaim = new IdentityUserClaim<string>()
            {
                UserId = jensVanOrden.Id,
                ClaimType = "User",
                ClaimValue = "User"
            };*/

            IdentityUserClaim<string> organisationClaim = new IdentityUserClaim<string>()
            {
                UserId = treecompany.Id,
                ClaimType = "Organisation",
                ClaimValue = "Organisation"
            };

            
            IdentityUserClaim<string> organisation2Claim = new IdentityUserClaim<string>()
            {
                UserId = havenbedrijf.Id,
                ClaimType = "Organisation",
                ClaimValue = "Organisation"
            };

            IdentityUserClaim<string> organisation3Claim = new IdentityUserClaim<string>()
            {
                UserId = stadantwerpen.Id,
                ClaimType = "Organisation",
                ClaimValue = "Organisation"
            };
            
            IdentityUserClaim<string> superadminVerifiedClaim = new IdentityUserClaim<string>()
            {
                UserId = treecompany.Id,
                ClaimType = "Verified",
                ClaimValue = "Verified"
            };  
            
            IdentityUserClaim<string> organisationVerifiedClaim = new IdentityUserClaim<string>()
            {
                UserId = havenbedrijf.Id,
                ClaimType = "Verified",
                ClaimValue = "Verified"
            };  
            
            IdentityUserClaim<string> organisationVerifiedClaim2 = new IdentityUserClaim<string>()
            {
                UserId = stadantwerpen.Id,
                ClaimType = "Verified",
                ClaimValue = "Verified"
            };  

            /*IdentityUserClaim<string> platformClaim3 = new IdentityUserClaim<string>()
            {
                UserId = disctrictMechelen.Id,
                ClaimType = "havenbedrijf",
                ClaimValue = "havenbedrijf"
            };*/
            
            PlatformRequest platformRequest1 = new PlatformRequest()
            {
                Reason = "Als google wil ik environment van werksfeer verbeteren door jullie site te gebruiken.",
                Accept = false,
                Date = DateTime.Now,
                Treated = false,
                UserId = robbeDumont.Id,
                OrganisationName = robbeDumont.FirmName
            };
            
            /*PlatformRequest platformRequest2 = new PlatformRequest()
            {
                Reason = "Wil gewoon mijn eigen platform voor mijn personeel.",
                Accept = false,
                Date = DateTime.Now,
                Treated = false,
                UserId = cronos.Id,
                OrganisationName = cronos.FirmName
            };*/

            ctx.Add(superadminClaim);
            ctx.Add(adminClaim);
            ctx.Add(admin2Claim);
            ctx.Add(modClaim);
            ctx.Add(mod2Claim);
//            ctx.Add(mod3Claim);
            ctx.Add(userClaim);
            ctx.Add(userClaim2);
            ctx.Add(userClaim3);
            ctx.Add(organisationClaim);
            ctx.Add(organisation2Claim);
            ctx.Add(organisation3Claim);
            ctx.Add(orgJosVermeulen);
            ctx.Add(orgJosVermeulen2);
//            ctx.Add(platformClaim1);
//            ctx.Add(platformClaim2);
//            ctx.Add(platformClaim3);
            ctx.Add(superadminVerifiedClaim);
            ctx.Add(organisationVerifiedClaim);
            ctx.Add(organisationVerifiedClaim2);
            ctx.Add(platformRequest1);
            #endregion

            #region ideations

            Ideation id1 = new Ideation()
            {
                CentralQuestion = "Hoe maken we de Groenplaats beter?",
                Description =
                    "Omdat er al lang niets meer vernieuwd is aan de Groenplaats wordt het eens tijd dat we er wat werk in steken. Welke dingen zou jij doen om de Groenplaats te verbeteren?",
                Url = "https://www.antwerpen.be/nl/",
                Questions = new List<Question>(),
                Replies = new List<IdeationReply>(),
                IdeationType = IdeationType.User,
                Project = p1
            };

            Ideation id3 = new Ideation()
            {
                CentralQuestion = "Hoe zullen de nieuwe parken er uit zien aan de kaaien?",
                Description = "Zoals jullie al weten zien de kaaien er niet goed uit, hoe lossen we dit op?",
                Url = "https://www.sigmaplan.be/",
                Questions = new List<Question>(),
                Replies = new List<IdeationReply>(),
                IdeationType = IdeationType.Admin,
                Project = p1
            };
            
            Ideation id4 = new Ideation()
            {
                CentralQuestion = "Hoe maken we de Turnhoutsebaan veiliger?",
                Description =
                    "Door onveilige situaties zien we ons genoodzaakt de Turnhoudsebaan te heraanleggen met een fietspad langs 1 kant over de gehele baan. Ook de trambedding willen we scheiden van de autoweg.",
                Url = "https://www.google.com/maps/place/Turnhoutsebaan,+Antwerpen/@51.2131326,4.4306199,14.72z/data=!4m5!3m4!1s0x47c3f77713d20a6f:0x532eaca34f1f9357!8m2!3d51.2185145!4d4.4515776",
                Questions = new List<Question>(),
                Replies = new List<IdeationReply>(),
                IdeationType = IdeationType.User,
                Project = p4
            };

            ctx.Ideations.Add(id1);
            ctx.Ideations.Add(id3);
            ctx.Ideations.Add(id4);

            ph2.Ideations.Add(id1);
            ph2.Ideations.Add(id3);
            ph7.Ideations.Add(id4);

            // Ideation vragen
            // Ideation 1
            Question id1Q1 = new Question()
            {
                QuestionString = "Wat vind je van de Groenplaats nu?",
                FieldType = FieldType.OpenText,
                Required = true,
                LongAnswer = true,
                OrderIndex = 0
            };
            Question id1Q2 = new Question()
            {
                QuestionString = "Naar waar zullen we het standbeeld verplaatsen?",
                FieldType = FieldType.Location,
                Location = new Location()
                {
                    Latitude = 51.21903498941106f,
                    Longitude = 4.401734329414531,
                    ZoomLevel = 19,
                    //AllowZoom = false
                },
                OrderIndex = 1
            };
            Question id1Q3 = new Question()
            {
                QuestionString = "Wat doe jij aan de Groenplaats?",
                Options = new List<string>
                {
                    "fietsen", "lopen", "rondwandelen", "op een bank zitten", "picknicken", "Ik kom er nooit", "Anders"
                },
                FieldType = FieldType.MultipleChoice,
                Required = true,
                OrderIndex = 2
            };
            Question id1Q4 = new Question()
            {
                QuestionString = "Hoe ziet de toekomstige Groenplaats er voor jou uit?",
                FieldType = FieldType.Image,
                OrderIndex = 3
            };
            Question id1Q5 = new Question()
            {
                QuestionString = "Beschrijf in één woord de Groenplaats",
                FieldType = FieldType.OpenText,
                Required = true,
                LongAnswer = false,
                OrderIndex = 4
            };
            Question id1Q6 = new Question()
            {
                QuestionString = "Is de Groenplaats goed zoals ze is?",
                Options = new List<string> {"Ja", "Neen", "Geen mening"},
                FieldType = FieldType.SingleChoice,
                Required = true,
                OrderIndex = 5
            };
            Question id1Q7 = new Question()
            {
                QuestionString = "Wat doe jij aan de Groenplaats?",
                Options = new List<string>
                {
                    "fietsen", "lopen", "rondwandelen", "op een bank zitten", "picknicken", "Ik kom er nooit", "Anders"
                },
                FieldType = FieldType.DropDown,
                Required = true,
                OrderIndex = 6
            };
            Question id1Q8 = new Question()
            {
                QuestionString = "Motivatie video",
                FieldType = FieldType.Video,
                OrderIndex = 7
            };

            id1.Questions.Add(id1Q1);
            id1.Questions.Add(id1Q2);
            id1.Questions.Add(id1Q3);
            id1.Questions.Add(id1Q4);
            id1.Questions.Add(id1Q5);
            id1.Questions.Add(id1Q6);
            id1.Questions.Add(id1Q7);
            id1.Questions.Add(id1Q8);

            // Ideation 2

            Question id2Q1 = new Question()
            {
                QuestionString = "Wat is het actieplan voor de nieuwe parken aan de kaaien",
                FieldType = FieldType.OpenText,
                Required = true,
                LongAnswer = true,
                OrderIndex = 0
            };
            Question id2Q2 = new Question()
            {
                QuestionString = "Waar zal het nieuwe park aangelegd worden?",
                FieldType = FieldType.Location,
                Location = new Location()
                {
                    Latitude = 51.215407,
                    Longitude = 4.391843,
                    ZoomLevel = 19,
                },
                OrderIndex = 1
            };
            Question id2Q3 = new Question()
            {
                QuestionString = "Wat zal er allemaal aanwezig zijn?",
                Options = new List<string>
                    {"Fietspaden", "Voetpaden", "Zitbanken", "Grasvelden", "Zwembaden", "Speeltuinen", "Barbecues"},
                FieldType = FieldType.MultipleChoice,
                Required = true,
                OrderIndex = 2
            };
            Question id2Q4 = new Question()
            {
                QuestionString = "Hoe zal het toekomtige park er uit zien?",
                FieldType = FieldType.Image,
                OrderIndex = 3
            };
            Question id2Q5 = new Question()
            {
                QuestionString = "Is de staat van de kaaien op dit moment goed?",
                Options = new List<string> {"Ja", "Neen", "Geen mening"},
                FieldType = FieldType.SingleChoice,
                Required = true,
                OrderIndex = 4
            };
            Question id2Q6 = new Question()
            {
                QuestionString = "Promo video",
                FieldType = FieldType.Video,
                OrderIndex = 5
            };

            id3.Questions.Add(id2Q1);
            id3.Questions.Add(id2Q2);
            id3.Questions.Add(id2Q3);
            id3.Questions.Add(id2Q4);
            id3.Questions.Add(id2Q5);
            id3.Questions.Add(id2Q6);

            //Ideation 4
            
            Question id4Q1 = new Question()
            {
                QuestionString = "Welk vervoersmiddel gebruik je?",
                Options = new List<string>
                {
                    "lopen", "fietsen", "e-bike", "e-step" , "auto", "bus", "tram"
                },
                FieldType = FieldType.MultipleChoice,
                Required = true,
                OrderIndex = 0
            };
            Question id4Q2 = new Question()
            {
                QuestionString = "Welk kruispunt voel jij je niet veilig?",
                FieldType = FieldType.Location,
                Required =false,
                OrderIndex = 1
            };
            Question id4Q3 = new Question()
            {
                QuestionString = "Welk vervoersmiddel verdient een prioriteit op een druk kruispunt?",
                Options = new List<string>
                {
                    "lopen", "fietsen", "e-bike", "e-step" , "auto", "bus", "tram"
                },
                FieldType = FieldType.DropDown,
                Required = true,
                OrderIndex = 2
            };
            Question id4Q4 = new Question()
            {
                QuestionString = "Heb je nog creatieve ideeën?",
                FieldType = FieldType.OpenText,
                Required = false,
                LongAnswer = true,
                OrderIndex = 3
            };
            
            id4.Questions.Add(id4Q1);
            id4.Questions.Add(id4Q2);
            id4.Questions.Add(id4Q3);
            id4.Questions.Add(id4Q4);
            #endregion

            #region ideation replies

            IdeationReply r1 = new IdeationReply()
            {
                Ideation = id1,
                Answers = new List<Answer>(),
                Title = "De Groenplaats moet grondig verbouwd worden",
                User = arneCools,
                Votes = new List<Vote>(),
                Comments = new List<Comment>(),
                Created = DateTime.Now.AddHours(-8),
                Reports = new List<IdeationReport>()
            };

            Random rnd = new Random();
            int nbrUpVotes = rnd.Next(1, 25);
            int nbrDownVotes = rnd.Next(1, 13);
            

            for (int j = 0; j < nbrUpVotes; j++)
            {
                int nbrOfHours = rnd.Next(0, 60);
                int nbrOfMinutes = rnd.Next(0, 60);
                var vote = r1.VoteUp(arneCools);

                var voteActivity = new Activity()
                {
                    ActivityTime = DateTime.Now.AddHours(-nbrOfHours).AddMinutes(-nbrOfMinutes),
                    ActivityType = ActivityType.IdeationVote,
                    User = arneCools,
                    Platform = plat1,
                    Vote = vote
                };
                ctx.Activities.Add(voteActivity);
            }

            for (int j = 0; j < nbrDownVotes; j++)
            {
                int nbrOfHours = rnd.Next(0, 60);
                int nbrOfMinutes = rnd.Next(0, 60);
                var vote = r1.VoteDown(arneCools);

                var voteActivity = new Activity()
                {
                    ActivityTime = DateTime.Now.AddHours(-nbrOfHours).AddMinutes(-nbrOfMinutes),
                    ActivityType = ActivityType.IdeationVote,
                    User = arneCools,
                    Platform = plat1,
                    Vote = vote
                };
                ctx.Activities.Add(voteActivity);
            }

            IdeationReport iReport1 = new IdeationReport()
            {
                User = arneCools
            };
            r1.Reports.Add(iReport1);

            for (var i = 0; i < 20; i++)
            {
                int nbrOfHours = rnd.Next(0, 60);
                int nbrOfMinutes = rnd.Next(0, 60);
                nbrUpVotes = rnd.Next(1, 25);
                nbrDownVotes = rnd.Next(1, 13);

                var r = new IdeationReply()
                {
                    Ideation = id1,
                    Answers = new List<Answer>(),
                    User = arneCools,
                    Votes = new List<Vote>(),
                    Comments = new List<Comment>(),
                    Created = DateTime.Now.AddHours(-nbrOfHours).AddMinutes(-nbrOfMinutes),
                    Title = "Er moet meer groen komen op de Groenplaats"
                };


                for (int j = 0; j < nbrUpVotes; j++)
                {
                    var vote = r.VoteUp(arneCools);

                    var voteActivity = new Activity()
                    {
                        ActivityTime = DateTime.Now.AddHours(-nbrOfHours).AddMinutes(-nbrOfMinutes),
                        ActivityType = ActivityType.IdeationVote,
                        User = arneCools,
                        Platform = plat1,
                        Vote = vote
                    };
                    ctx.Activities.Add(voteActivity);
                }

                for (int j = 0; j < nbrDownVotes; j++)
                {
                    var vote = r.VoteDown(arneCools);

                    var voteActivity = new Activity()
                    {
                        ActivityTime = DateTime.Now,
                        ActivityType = ActivityType.IdeationVote,
                        User = arneCools,
                        Platform = plat1,
                        Vote = vote
                    };
                    ctx.Activities.Add(voteActivity);
                }

                id1.Replies.Add(r);
            }

            IdeationReply r2 = new IdeationReply()
            {
                Ideation = id1,
                Answers = new List<Answer>(),
                User = arneCools,
                Votes = new List<Vote>(),
                Comments = new List<Comment>(),
                Created = DateTime.Now.AddHours(-9),
                Title = "testtile"
            };

            //Answers voor ideation reply
            Answer a1 = new OpenTextAnswer()
            {
                Value = "Misschien moeten we de putjes eens vullen zodat mensen hun benen niet breken",
                QuestionIndex = 0,
                OrderIndex = 0,
            };

            Answer a2 = new LocationAnswer()
            {
                Value = new Location()
                {
                    Latitude = 51.212684f,
                    Longitude = 4.412426f,
                    ZoomLevel = 18
                },
                QuestionIndex = 1,
                OrderIndex = 1
            };

            Answer a3 = new MultipleChoiceAnswer()
            {
                SelectedChoices = new List<bool>() {true, false, false, false, false, true, true},
                QuestionIndex = 2,
                OrderIndex = 2
            };

            //Geen antwoord op de vierde vraag dus daarom staat hij er niet bij (niet required)

            Answer a4 = new OpenTextAnswer()
            {
                Value = "groot",
                QuestionIndex = 4,
                OrderIndex = 3
            };

            Answer a5 = new SingleChoiceAnswer()
            {
                SelectedChoice = 1,
                QuestionIndex = 5,
                OrderIndex = 4
            };

            Answer a6 = new SingleChoiceAnswer()
            {
                SelectedChoice = 2,
                QuestionIndex = 6,
                OrderIndex = 5
            };
            Answer a7 = new MediaAnswer()
            {
                QuestionIndex = 3,
                OrderIndex = 6,
                Value = new Media()
                {
                    Name = "Foto van de Groenplaats",
                    Url =
                        "https://www.vlaamsbouwmeester.be/sites/default/files/styles/large/public/open_call_project_images_award/beeld-website-9.jpg"
                }
            };

            r1.Answers.Add(a1);
            r1.Answers.Add(a2);
            r1.Answers.Add(a3);
            r1.Answers.Add(a4);
            r1.Answers.Add(a5);
            r1.Answers.Add(a6);
            r1.Answers.Add(a7);

            IdeationReply r3 = new IdeationReply()
            {
                Ideation = id3,
                Answers = new List<Answer>(),
                User = havenbedrijf,
                Title = "We willen meer groen op de kaaien",
                Votes = new List<Vote>(),
                Comments = new List<Comment>(),
                Created = DateTime.Now.AddHours(-50),
                Reports = new List<IdeationReport>()
            };
            
            nbrUpVotes = rnd.Next(1, 25);
            nbrDownVotes = rnd.Next(1, 13);
            

            for (int j = 0; j < nbrUpVotes; j++)
            {
                int nbrOfHours = rnd.Next(0, 60);
                int nbrOfMinutes = rnd.Next(0, 60);
                var vote = r3.VoteUp(arneCools);

                var voteActivity = new Activity()
                {
                    ActivityTime = DateTime.Now.AddHours(-nbrOfHours).AddMinutes(-nbrOfMinutes),
                    ActivityType = ActivityType.IdeationVote,
                    User = arneCools,
                    Platform = plat1,
                    Vote = vote
                };
                ctx.Activities.Add(voteActivity);
            }

            for (int j = 0; j < nbrDownVotes; j++)
            {
                int nbrOfHours = rnd.Next(0, 60);
                int nbrOfMinutes = rnd.Next(0, 60);
                var vote = r3.VoteDown(arneCools);

                var voteActivity = new Activity()
                {
                    ActivityTime = DateTime.Now.AddHours(-nbrOfHours).AddMinutes(-nbrOfMinutes),
                    ActivityType = ActivityType.IdeationVote,
                    User = arneCools,
                    Platform = plat1,
                    Vote = vote
                };
                ctx.Activities.Add(voteActivity);
            }

            Answer r3A1 = new OpenTextAnswer()
            {
                Value =
                    "Het ontwerp voor het Droogdokkenpark vertrekt vanuit een groot respect voor de bestaande kwaliteit van de plek en behoudt ook sterk het huidige karakter van de site. De komende jaren krijgt het nieuwe park tussen stad en haven een natuurlijke getijdenoever langs de Schelde, waar zich slikken en schorren zullen vormen. Er komt ook een avontuurlijke speelzone en een park met een natuurlijk amfitheater voor kleine evenementen.",
                QuestionIndex = 0,
                OrderIndex = 0,
            };

            Answer r3A2 = new LocationAnswer()
            {
                Value = new Location()
                {
                    Latitude = 51.215407,
                    Longitude = 4.391843,
                    ZoomLevel = 15
                },
                QuestionIndex = 1,
                OrderIndex = 1
            };

            Answer r3A3 = new MultipleChoiceAnswer()
            {
                SelectedChoices = new List<bool>() {true, true, true, true, true, true, true},
                QuestionIndex = 2,
                OrderIndex = 2
            };


            Answer r3A4 = new MediaAnswer()
            {
                Value = new Media()
                {
                    Url =
                        "https://www.agvespa.be/sites/default/files/styles/header_images_slider/public/header-images/droogdokkenpark_belvedere_c_ag_vespa_bart_gosselin.jpg?itok=J-OhSQ3N",
                    Name = "Foto van het nieuwe park"
                },
                QuestionIndex = 3,
                OrderIndex = 3
            };

            Answer r3A5 = new SingleChoiceAnswer()
            {
                SelectedChoice = 2,
                QuestionIndex = 4,
                OrderIndex = 4
            };
            Answer r3A6 = new MediaAnswer()
            {
                QuestionIndex = 5,
                OrderIndex = 5,
                Value = new Media()
                {
                    Name = "Video van het nieuwe park",
                    Url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                }
            };
            r3.Answers.Add(r3A1);
            r3.Answers.Add(r3A2);
            r3.Answers.Add(r3A3);
            r3.Answers.Add(r3A4);
            r3.Answers.Add(r3A5);
            r3.Answers.Add(r3A6);

            id3.Replies.Add(r3);

            #endregion

            #region votes & comments

            Vote v1 = new Vote()
            {
                Anonymous = true,
                Created = DateTime.Now,
                Value = true,
            };
            Vote v2 = new Vote()
            {
                Anonymous = true,
                Created = DateTime.Now,
                Value = true,
            };

            Comment c1 = new Comment
            {
                User = arneCools,
                CommentText = "Dat vind ik ook!",
                Created = DateTime.Now.AddHours(-4),
                Hidden = false,
                Reports = new List<Report>()
            };
            var commentActivity1 = new Activity()
            {
                ActivityTime = DateTime.Now.AddHours(-4),
                ActivityType = ActivityType.Comment,
                User = arneCools,
                Platform = plat1,
                Comment = c1
            };

            Comment c2 = new Comment
            {
                User = havenbedrijf,
                CommentText = "Oei nee totaal niet mee eens.",
                Created = DateTime.Now,
                Hidden = false,
                Reports = new List<Report>()
            };
            var commentActivity2 = new Activity()
            {
                ActivityTime = DateTime.Now,
                ActivityType = ActivityType.Comment,
                User = havenbedrijf,
                Platform = plat1,
                Comment = c2
            };

            Comment c3 = new Comment
            {
                User = havenbedrijf,
                CommentText = "Da trekt op niks jom",
                Created = DateTime.Now.AddHours(-48),
                Hidden = false,
                Reports = new List<Report>()
            };
            var commentActivity3 = new Activity()
            {
                ActivityTime = DateTime.Now.AddHours(-48),
                ActivityType = ActivityType.Comment,
                User = havenbedrijf,
                Platform = plat1,
                Comment = c3
            };

            Comment c4 = new Comment
            {
                User = havenbedrijf,
                CommentText = "**** (offensive comment) ***",
                Created = DateTime.Now.AddHours(-48),
                Hidden = false,
                Reports = new List<Report>()
            };
            var commentActivity4 = new Activity()
            {
                ActivityTime = DateTime.Now.AddHours(-48),
                ActivityType = ActivityType.Comment,
                User = havenbedrijf,
                Platform = plat1,
                Comment = c4
            };
            ctx.Activities.Add(commentActivity4);


            r1.Votes.Add(v1);
            r1.Votes.Add(v2);
            r1.Comments.Add(c1);
            ctx.Activities.Add(commentActivity1);
            r1.Comments.Add(c2);
            ctx.Activities.Add(commentActivity2);
            r1.Comments.Add(c3);
            ctx.Activities.Add(commentActivity3);
            r1.Comments.Add(c4);
            ctx.Activities.Add(commentActivity4);
            id1.Replies.Add(r1);
            //id1.Replies.Add(r2);

            #endregion

            #region reports

            Report report1 = new Report()
            {
                Reason = "Ik voel me beledigd",
                ReportedComment = c4,
                User = treecompany
            };

            Report report2 = new Report()
            {
                Reason = "Geen respect",
                ReportedComment = c3,
                User = jensVanOrden
            };

            Report report3 = new Report()
            {
                Reason = "Dit kan gewoon niet",
                ReportedComment = c4,
                User = arneCools
            };

            Report report4 = new Report()
            {
                Reason = "Alei gast",
                ReportedComment = c4,
                User = stadantwerpen
            };

            c3.Reports.Add(report2);
            c4.Reports.Add(report1);
            c4.Reports.Add(report3);
            c4.Reports.Add(report4);

            #endregion

            #region forms

            //Form 1
            Form f1 = new Form()
            {
                Title = "Hoe moet de kaai verbouwd worden",
                Questions = new List<Question>(),
                Replies = new List<FormReply>(),
                Project = p1
            };

            ph2.Forms.Add(f1);

            Question fq1 = new Question()
            {
                QuestionString = "Moet er een nieuwe fietsersbrug naar linkeroever komen?",
                FieldType = FieldType.SingleChoice,
                Options = new List<string> {"ja", "nee"},
                Required = true,
                OrderIndex = 0
            };

            Question fq2 = new Question()
            {
                QuestionString = "Wat doe jij zoal aan de kaai?",
                FieldType = FieldType.DropDown,
                Options = new List<string>
                    {"fietsen", "lopen", "rondwandelen", "op een bank zitten", "picknicken", "Niet van toepassing"},
                Required = true,
                OrderIndex = 1
            };

            Question fq3 = new Question()
            {
                QuestionString = "Wat doe jij zoal aan de kaai?",
                FieldType = FieldType.MultipleChoice,
                Options = new List<string>
                    {"fietsen", "lopen", "rondwandelen", "op een bank zitten", "picknicken", "Niet van toepassing"},
                Required = true,
                OrderIndex = 2
            };

            Question fq4 = new Question()
            {
                QuestionString = "Heb je nog meer ideeën?",
                FieldType = FieldType.OpenText,
                Required = false,
                OrderIndex = 3
            };

            f1.Questions.Add(fq1);
            f1.Questions.Add(fq2);
            f1.Questions.Add(fq3);
            f1.Questions.Add(fq4);

            ctx.Forms.Add(f1);


            //form 2
            Form f2 = new Form()
            {
                Title = "Wat vindt u van de nieuwe tramsporen?",
                Project = p1
            };

            ph2.Forms.Add(f2);


            Question f2Q1 = new Question()
            {
                QuestionString = "Wat vindt u van fietsvriendelijke tramsporen?",
                FieldType = FieldType.OpenText,
                Required = true,
                OrderIndex = 0
            };

            Question f2Q2 = new Question()
            {
                QuestionString = "Wat vind je van de tramsporen nu?",
                FieldType = FieldType.OpenText,
                Required = false,
                LongAnswer = true,
                OrderIndex = 1
            };

            Question f2Q3 = new Question()
            {
                QuestionString = "Zijn er genoeg tramlijnen volgens u?",
                FieldType = FieldType.SingleChoice,
                Options = new List<string> {"ja", "nee"},
                Required = true,
                OrderIndex = 2
            };

            Question f2Q4 = new Question()
            {
                QuestionString = "Naar waar gaat u met de tram?",
                FieldType = FieldType.MultipleChoice,
                Options = new List<string> {"werk", "school", "winkel", "sport", "naar het park", "naar het station"},
                Required = false,
                OrderIndex = 3
            };

            f2.Questions.Add(f2Q1);
            f2.Questions.Add(f2Q2);
            f2.Questions.Add(f2Q3);
            f2.Questions.Add(f2Q4);
            ctx.Forms.Add(f2);
            
            //reply 1
            
            FormReply reply1 = new FormReply()
            {
                User = arneCools,
                Anonymous = false,
                Form = f2
            };
            
            Answer r1a1 = new OpenTextAnswer()
            {
                OrderIndex = 0,
                QuestionIndex = 0,
                Value = "Dit mag zeker worden ingevoerd, nu zijn de tramsporen veel te gevaarlijk!"
            };
            reply1.Answers.Add(r1a1);
            
            Answer r1a2 = new OpenTextAnswer()
            {
                OrderIndex = 1,
                QuestionIndex = 1,
                Value = "Levensgevaarlijk, ik heb al drie keer mijn benen gebroken"
            };
            reply1.Answers.Add(r1a2);
            
            Answer r1a3 = new SingleChoiceAnswer()
            {
                OrderIndex = 2,
                QuestionIndex = 2,
                SelectedChoice = 1
            };
            reply1.Answers.Add(r1a3);
            
            Answer r1a4 = new MultipleChoiceAnswer()
            {
                OrderIndex = 3,
                QuestionIndex = 3,
                SelectedChoices = new List<bool> {false, true, false, true, false, true},
            };
            reply1.Answers.Add(r1a4);
            f2.Replies.Add(reply1);
            
            //reply 2
            
            FormReply reply2 = new FormReply()
            {
                User = stadantwerpen,
                Anonymous = false,
                Form = f2
            };
            
            Answer r2a1 = new OpenTextAnswer()
            {
                OrderIndex = 0,
                QuestionIndex = 0,
                Value = "Dat is een goed idee"
            };
            reply2.Answers.Add(r2a1);
            
            Answer r2a2 = new OpenTextAnswer()
            {
                OrderIndex = 1,
                QuestionIndex = 1,
                Value = "Ik rij zelf niet met de fiets in de stad, maar ik begrijp wel dat tramsporen voor fietsers wel gevaarlijk is"
            };
            reply2.Answers.Add(r2a2);
            
            Answer r2a3 = new SingleChoiceAnswer()
            {
                OrderIndex = 2,
                QuestionIndex = 2,
                SelectedChoice = 1
            };
            reply2.Answers.Add(r2a3);
            
            Answer r2a4 = new MultipleChoiceAnswer()
            {
                OrderIndex = 3,
                QuestionIndex = 3,
                SelectedChoices = new List<bool> {true, false, true, true, false, true},
            };
            reply2.Answers.Add(r2a4);
            f2.Replies.Add(reply2);
            
            //reply 3
            
            FormReply reply3 = new FormReply()
            {
                User = jensVanOrden,
                Anonymous = false,
                Form = f2
            };
            
            Answer r3a1 = new OpenTextAnswer()
            {
                OrderIndex = 0,
                QuestionIndex = 0,
                Value = "Da interesseert me niet"
            };
            reply3.Answers.Add(r3a1);
            
            Answer r3a2 = new OpenTextAnswer()
            {
                OrderIndex = 1,
                QuestionIndex = 1,
                Value = "Ik rij alleen met de auto"
            };
            reply3.Answers.Add(r3a2);
            
            Answer r3a3 = new SingleChoiceAnswer()
            {
                OrderIndex = 2,
                QuestionIndex = 2,
                SelectedChoice = 0
            };
            reply3.Answers.Add(r3a3);
            
            Answer r3a4 = new MultipleChoiceAnswer()
            {
                OrderIndex = 3,
                QuestionIndex = 3,
                SelectedChoices = new List<bool> {false, false, false, false, false, false},
            };
            reply3.Answers.Add(r3a4);
            f2.Replies.Add(reply3);
            
            //reply 4
            
            FormReply reply4 = new FormReply()
            {
                User = treecompany,
                Anonymous = false,
                Form = f2
            };
            
            Answer r4a1 = new OpenTextAnswer()
            {
                OrderIndex = 0,
                QuestionIndex = 0,
                Value = "'t is allemaal de schuld van de sossen"
            };
            reply4.Answers.Add(r4a1);
            
            Answer r4a3 = new SingleChoiceAnswer()
            {
                OrderIndex = 2,
                QuestionIndex = 2,
                SelectedChoice = 0
            };
            reply4.Answers.Add(r4a3);
            
            Answer r4a4 = new MultipleChoiceAnswer()
            {
                OrderIndex = 3,
                QuestionIndex = 3,
                SelectedChoices = new List<bool> {false, false, false, false, false, false},
            };
            reply4.Answers.Add(r4a4);
            f2.Replies.Add(reply4);

            // Statement form
            Form f3 = new Form()
            {
                IsStatementForm = true,
                Questions = new List<Question>(),
                Title = "De huidige staat van de Groenplaats",
                Project = p1
            };
            Question f3Q = new Question()
            {
                QuestionString = "Is de Groenplaats goed zoals ze nu is?",
                FieldType = FieldType.Statement,
                OrderIndex = 0,
                Options = new List<string> {"Tegen","Voor"}
            };
            f3.Questions.Add(f3Q);
            ph2.Forms.Add(f3);
            ctx.Forms.Add(f3);
            
            rnd = new Random();
            nbrUpVotes = rnd.Next(1, 25);
            nbrDownVotes = rnd.Next(1, 13);

            for (int i = 0; i < nbrUpVotes; i++)
            {
                FormReply reply = new FormReply()
                {
                    Answers = new List<Answer>(),
                    Anonymous = true,
                    Form = f3
                };
                
                Answer answer = new SingleChoiceAnswer()
                {
                    SelectedChoice = 1
                };
                reply.Answers.Add(answer);
                
                f3.Replies.Add(reply);
            }

            for (int i = 0; i < nbrDownVotes; i++)
            {
                FormReply reply = new FormReply()
                {
                    Answers = new List<Answer>(),
                    Anonymous = true,
                    Form = f3
                };
                
                Answer answer = new SingleChoiceAnswer()
                {
                    SelectedChoice = 0
                };
                reply.Answers.Add(answer);
                
                f3.Replies.Add(reply);
            }

            #endregion
            
            #region form replies
            
            
            
            #endregion

            #region iot-links

            // Iot link voor ideation reply 1
            IotLink link1 = new IotLink()
            {
                IdeationReply = r3,
                Location = new Location()
                {
                    Latitude = 51.214657265964945,
                    Longitude = 4.391249052998174,
                    ZoomLevel = 16,
                },
                Project = p1,
            };
            ctx.IotLinks.Add(link1);

            ctx.SaveChanges();

            IotLink link2 = new IotLink()
            {
                Form = f3,
                Location = new Location()
                {
                    Latitude = 51.21906130210496,
                    Longitude = 4.401592172336661,
                    ZoomLevel = 19,
                },
                Project = p1,
            };
            ctx.IotLinks.Add(link2);

            #endregion

            // Save the changes in the context to the database
            ctx.SaveChanges();

            // Detach all entities from the context,
            // else this data stays attached to context and should be read from the db when needed!
            foreach (EntityEntry entry in ctx.ChangeTracker.Entries().ToList())
            {
                entry.State = EntityState.Detached;
            }
        }
    }
}