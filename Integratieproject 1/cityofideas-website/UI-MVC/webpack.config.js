const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const launchSettings = require('./Properties/launchSettings.json');
const CopyWebpackPlugin = require('copy-webpack-plugin');

function getEnvironment() {
    // Try to parse the environment from the launchsettings.
    // If not found, default it to 'development'
    const env = process.env.ASPNETCORE_ENVIRONMENT ||
        (launchSettings &&
        launchSettings.profiles['UI_MVC'].environmentVariables.ASPNETCORE_ENVIRONMENT) ||
        'development';

    return env.toLowerCase();
}

function getPostCssPlugins() {
    const plugins = [
        require('autoprefixer'),
    ];

    if (getEnvironment() === 'production') {
        // in production also minify the css
        plugins.push(require('cssnano')({ preset: 'default' }));
    }

    return plugins;
}

console.log(`Webpack environment: ${getEnvironment()}`);

module.exports = {
    mode: getEnvironment(),
    entry: {
        // general
        site: './wwwroot/js/site.js',
        bootstrap_js: './wwwroot/js/bootstrap_js.js',
        validation: './wwwroot/js/validation.js',
        index: './wwwroot/js/index.js',
        search: './wwwroot/js/search.js',
        platforms: './wwwroot/js/platforms.js',
        colorSchemeLoader: './wwwroot/js/colorSchemeLoader.js',

        searchIndex: './wwwroot/js/users/searchIndex.js',
        constants: './wwwroot/js/constants.js',

        //User
        registerUser: './wwwroot/js/users/registerUser.js',
        loginUser: './wwwroot/js/users/loginUser.js',

        // Admin
        admin: './wwwroot/js/admin/index.js',
        dashboard: './wwwroot/js/admin/dashboard.js',
        showUsers: './wwwroot/js/users/showUsers.js',
        showVerifyrequests: './wwwroot/js/users/showVerifyrequests.js',
        showOrganisations: './wwwroot/js/users/showOrganisations.js',
        reportPanel: './wwwroot/js/users/reportPanel.js',
        showPlatformRequests: './wwwroot/js/users/showPlatformRequests.js',

        // Platform
        indexPlatform:  './wwwroot/js/platforms/index.js',
        createPlatform: './wwwroot/js/platforms/create.js',

        // Project
        createProject: './wwwroot/js/projects/create.js',
        projectDetails: './wwwroot/js/projects/details.js',
        createIoT: './wwwroot/js/projects/createIoT.js',

        // Ideation
        createIdeation: './wwwroot/js/ideations/create.js',
        replyIdeation: './wwwroot/js/ideations/reply.js',
        viewIdeation: './wwwroot/js/ideations/view.js',
        createIdeationValidation: './wwwroot/js/ideations/createIdeationValidation.js',
        ideationReplyOverview: './wwwroot/js/ideations/replyOverview.js',
        comments: './wwwroot/js/ideations/comments.js',

        // Form
        createForm: './wwwroot/js/forms/create.js',
        replyForm: './wwwroot/js/forms/reply.js',
        formValidation: './wwwroot/js/forms/validation.js',
        createFormValidation: './wwwroot/js/forms/createFormValidation.js',
        formCreatorValidation: './wwwroot/js/forms/formCreatorValidation.js',
        result: './wwwroot/js/forms/result.js',
        
        //iot
        iot: './wwwroot/js/iot.js',
    },
    
    output: {
        filename: '[name].entry.js',
        path: __dirname + '/wwwroot/dist',
    },
    devtool: 'source-map',
    module: {
        rules: [
            {
                test: /\.m?js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env']
                    }
                }
            },
            {
                test: /\.(sc|c)ss$/,
                use: [
                    { loader: MiniCssExtractPlugin.loader },
                    'css-loader',
                    {
                        loader: 'postcss-loader',
                        options: {
                            ident: 'postcss',
                            plugins: getPostCssPlugins(),
                        }
                    },
                    'sass-loader',
                ],
            },
            { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: "file-loader" },
            { test: /\.(woff|woff2)$/, loader: "url-loader?prefix=font/&limit=5000" },
            { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: "url-loader?limit=10000&mimetype=application/octet-stream" },
            {
                test: /\.(gif|png|jpe?g|svg)$/i,
                use: [
                    'file-loader',
                    {
                        loader: 'image-webpack-loader',
                    },
                ],
            }
        ],
    },
    plugins: [
        new MiniCssExtractPlugin({
            filename: "[name].css",
        }),
        // owlcarousel needs this type of plugin
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery'
        }),
        new CopyWebpackPlugin([
            { from: 'wwwroot/images', to: '.'}
        ]),
    ],
};
