import $ from 'jquery';
import counterUp from 'counterup2';
import * as signalR from '@aspnet/signalr';
import * as util from '../util';
import Flickity from 'flickity/dist/flickity.pkgd.min';
import {initLazyLoad} from "../util";
import {sleep} from "../util";
import {timeAgo} from "../util";
import axios from "axios";
import IotLinkType from "../IotLinkType";
import {LoadIoT} from "../iot";
//import 'slick-carousel/slick/slick.min.js';

let feedCarousel;

function setupCarousel(){
    document.getElementById('projects-loader').remove();
    const counterEl = document.querySelector('.counter');

    if (counterEl !== null) {
        counterUp(counterEl, {
            duration: 1000,
            delay: 16,
        });
    }
    
    const activeWrapper = document.getElementById('active-projects');

    const activeProjects = Array.from(document.getElementsByClassName('activeProject'));
     
    if (activeProjects.length > 2) {
        activeProjects.forEach((element) => {
            element.classList.add('carousel-cell');
            element.style.opacity = 100;
        });
        var flkty = new Flickity( activeWrapper, {
            // options
            //cellAlign: 'left',
            // contain: true,
            wrapAround: true,
            //  groupCells: 2
        });
    }else if(activeProjects.length > 1){
        
        activeWrapper.classList.add('row');
        activeProjects.forEach((card) => {
            card.style.opacity = 100;
            const cardWrapper = document.createElement('div');
            cardWrapper.classList.add('col-md-6');
            cardWrapper.classList.add('col-sm-12');
            card.parentNode.appendChild(cardWrapper);
            cardWrapper.appendChild(card);
            
        });
    }else if(activeProjects.length > 0){
        activeProjects[0].style.opacity = 100;
        activeProjects[0].parentNode.classList.add('justify-content-center');
        activeProjects[0].parentNode.classList.add('d-flex');
        activeProjects[0].classList.add('col-md-6');
        activeProjects[0].classList.add('col-sm-12');
    }

    const finishedWrapper = document.getElementById('finished-projects');

    const finishedProjects = Array.from(document.getElementsByClassName('finishedProject'));

    if (finishedProjects.length > 2) {
        finishedProjects.forEach((element) => {
            element.classList.add('carousel-cell');
        });
        var flkty2 = new Flickity( finishedWrapper, {
            // options
            //cellAlign: 'left',
            // contain: true,
            wrapAround: true,
            //  groupCells: 2
        });
    }else if(finishedProjects.length > 1){

        finishedWrapper.classList.add('row');
        finishedProjects.forEach((card) => {
            const cardWrapper = document.createElement('div');
            cardWrapper.classList.add('col-md-6');
            cardWrapper.classList.add('col-sm-12');
            card.parentNode.appendChild(cardWrapper);
            cardWrapper.appendChild(card);

        });
    }else if(finishedProjects.length > 0){
        finishedProjects[0].parentNode.classList.add('justify-content-center');
        finishedProjects[0].parentNode.classList.add('d-flex');
        finishedProjects[0].classList.add('col-md-6');
        finishedProjects[0].classList.add('col-sm-12');
    }
    
    
    const descriptions = Array.from(document.getElementsByClassName("description"));
    
    descriptions.forEach((description) => {
        if (description.innerText.length > 100) {
           // const nextSpace = description.innerText.
            description.innerText = description.innerText.substr(0,100) + "...";
        }
        
    });

    initLazyLoad();

    $("#afgelopen").hide();

    sleep(100);
    $(window).trigger('resize');
}

$('#active-button').click(function () {
    $('.tab-slider--tabs').removeClass('slide');
    $('#afgelopen').hide();
    $('#actief').fadeIn();
   
    $(".tab-slider--nav li").removeClass("active");
    $(this).addClass('active');
});

$('#inactive-button').click(function () {
    $('.tab-slider--tabs').addClass('slide');
    $('#actief').hide();
    $('#afgelopen').fadeIn();


    $(".tab-slider--nav li").removeClass("active");
    $(this).addClass('active');
});

let connection;

function loadProjectStats(project) {
    const projectId = project.getElementsByTagName('input')[0].value;
    const ideationReplyCount = project.getElementsByClassName('ideationReplyCount')[0];
    const voteCount = project.getElementsByClassName('voteCount')[0];
    const commentCount = project.getElementsByClassName('commentCount')[0];
    
    const uri = `/api/project/stats/${projectId}`;

    ideationReplyCount.innerHTML = '';
    voteCount.innerHTML = "";
    commentCount.innerHTML = "";
    
    axios.get(uri)
        .then((r) => {
            ideationReplyCount.innerHTML = r.data.ideationReplyCount;
            voteCount.innerHTML = r.data.voteCount;
            commentCount.innerHTML = r.data.commentCount;
        })
        .catch((err) => {
           console.log('Er ging iets mis bij het ophalen van de project statistieken:');
           console.log(err);
        });
    
}

function loadProjectsStats() {
    const projects = Array.from(document.getElementsByClassName('project'));
    projects.forEach(loadProjectStats);
}

async function init() {
    connection = new signalR.HubConnectionBuilder().withUrl('/activityhub').build();

    connection.on('UpdateActivityFeed', (activity) => {
        updateActivityFeed(activity)
    });

    await connection.start();
    connection.invoke('JoinPage', util.getSubdomainTenant());

    const feed = document.getElementById('activity-feed');

    const dates = Array.from(document.getElementsByClassName("activity-date"));
    dates.forEach((date) => {
        const timeAgoText = timeAgo(new Date(date.innerText));
        date.innerText = timeAgoText;
    });

    feed.classList.remove('d-none');

    feedCarousel = new Flickity( feed, {
        // options
        //cellAlign: 'left',
        // contain: true,
        wrapAround: true,
        autoPlay: 3000,
        prevNextButtons: false,
        pageDots: false
        //  groupCells: 2
    });

    const platfomrId = document.getElementById('platform-id').value;
    if (document.getElementById('iot-count')) {
        LoadIoT("platform", platfomrId);
    }
    await sleep(250);
    setupCarousel();
    await sleep(1500);
    loadProjectsStats();
    
    
}

function updateActivityFeed(activity) {
    const feed = document.getElementById('activity-feed');
    
    const timeAgoText = timeAgo(activity.activityDate);
    console.log(timeAgoText);

    const entry = document.createElement('div');
    entry.className = 'activity-entry w-100';

    const content = document.createElement('div');
/*    content.innerHTML = `<span>${activity.user}</span>
        heeft ${activity.action} 
        <span>${activity.value}<span> 
        - <span class="activity-date">${activity.activityDate}</span>`;*/
    
    content.innerHTML = `<p class="mb-0">
                                                <span>${activity.user}</span>
                                                heeft
                                                ${activity.action}
                                                <span><a href="@activity.GetLink()"</a>${activity.value}</span>
                                            </p>
                                            <p class="mb-0 small"><span class="activity-date font-italic ">${timeAgoText}</span></p>`;

    entry.appendChild(content);
    feed.appendChild(entry);

    feed.scrollTop = feed.scrollHeight;
    
    feedCarousel.append(entry);
}

function cleanUp() {
    connection.invoke('LeavePage', util.getSubdomainTenant());
}

window.onload = init;
window.onunload = cleanUp;