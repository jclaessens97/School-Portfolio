import $ from 'jquery';

$(document).ready(() => {
    $('#finished-projects').hide();
    $('#active-projects').fadeIn();
});

$('#active').click(() => {
    $('#finished-projects').hide();
    $('#active-projects').fadeIn();
    $(this).addClass('active');
    $('#finished-projects').removeClass('active');
});

$('#finished').click(() => {
    $('#active-projects').hide();
    $('#finished-projects').fadeIn();
    $(this).addClass('active');
    $('#active-projects').removeClass('active');
});
