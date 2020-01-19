module.exports = {
    root: true,
    env: {
        browser: true,
        es6: true,
    },
    extends: [
        'airbnb-base',
    ],
    rules: {
        'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
        'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
        'no-alert': process.env.NODE_ENV === 'production' ? 'error' : 'off',
        'import/prefer-default-export': 'off',
        'indent': ["error", 4],
        'no-plusplus': 'off',
        'no-param-reassign': 'off',
    }
};
