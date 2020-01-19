import * as signalR from "@aspnet/signalr";
import IotLinkType from "./IotLinkType";
import axios from "axios";
import Flickity from 'flickity/dist/flickity.pkgd.min';
import {getCookie} from "./util";

let iotCarousel;

let iotCount;
let iotsLoaded = 0;


function setVoted(iotVotesObject) {
    iotVotesObject.feedbackWrapper.innerText = "Bedankt voor je stem!";
    iotVotesObject.feedbackWrapper.classList.add('italic');
    iotVotesObject.feedbackWrapper.classList.add('mt-2');
}

function voteForm(iotVotesObject,formId, agree) {
    setVoted(iotVotesObject);
    
    const url = `/api/forms/vote/${formId}/${agree}`;

    axios.post(url)
        .then((r) => {
            console.log(r.data);
            iotVotesObject.upvotes = r.data.upvotes;
            iotVotesObject.downvotes = r.data.downvotes;
            document.cookie = `voted${formId}=true`;
            UpdateVoteCount(iotVotesObject);
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het voten:');
            console.log(err);
        })
}


function UpdateVoteCount(iotVotesObject) {
    const upvotes = iotVotesObject.upvotes;
    const downvotes = iotVotesObject.downvotes;
    const totalVotes = upvotes+downvotes;
    const upvotesScaled =  (totalVotes > 0) ? +upvotes / +totalVotes * 100 : 0;
    const downvotesScaled = (totalVotes > 0) ? +downvotes / +totalVotes * 100 : 0;

    iotVotesObject.upVotesText.innerText = upvotes;
    iotVotesObject.downVotesText.innerText = downvotes;
    iotVotesObject.upVotesProgress.style.width = upvotesScaled + '%';
    iotVotesObject.downVotesProgress.style.width = downvotesScaled + '%';


}


async function ShowIoT(iot) {
    const location = iot.location;
    const upvotes = iot.upVotes;
    const downvotes = iot.downVotes;
    const question = iot.question;


    const ioTWrapper = document.getElementById('iot-wrapper');

    const iotTCard = document.createElement('div');

    iotTCard.innerHTML = `<div style="width: 1000px" class="container mt-0  mb-3 carousel-cell">
            <div class="row">
                <div class="container">
                    <div class="row">
                        <div class="col-4">
                            <img class="media" src="https://maps.googleapis.com/maps/api/staticmap?center=${location.latitude},${location.longitude}&zoom=${location.zoomLevel}&size=600x300&markers=color:red%7C${location.latitude},${location.longitude}&key=AIzaSyAcjLH-kOiE6TOc84tcRYRewC9bPiRKqvo"/>
                        </div>
                        <div class="col-8">
                            <h3>${question}</h3>
                            <div class="w-50">
                                <div class="d-flex justify-content-between">
                                <p class="h6"><span class="fas fa-arrow-up mr-2"></span><span class="votes-up"></span></p>
                                <p class="h6"><span class="fas fa-arrow-down mr-2"></span><span class="votes-down"></span></p>
                                </div>
                                <div class="progress w-100 big-bar">
                                    <div class="progress-bar votes-up-progress"></div>
                                    <div class="progress-bar bg-danger votes-down-progress"></div>
                                </div>
                                <!--<div class="w-100 d-flex justify-content-between">
                                    <div><span>Voor</span></div>
                                    <div><span class="text-right">Tegen</span></div>
                                </div>-->
                                <div class="feedback-wrapper"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>`;


    ioTWrapper.appendChild(iotTCard);
    const upVotesText = iotTCard.getElementsByClassName('votes-up')[0];
    const downVotesText = iotTCard.getElementsByClassName('votes-down')[0];

    const upVotesProgress = iotTCard.getElementsByClassName('votes-up-progress')[0];
    const downVotesProgress = iotTCard.getElementsByClassName('votes-down-progress')[0];

    const feedbackWrapper = iotTCard.getElementsByClassName('feedback-wrapper')[0];
    
    const iotVotesObject = {
        upVotesText,
        downVotesText,
        upVotesProgress,
        downVotesProgress,
        upvotes,
        downvotes,
        feedbackWrapper
    };
    
    const connection = new signalR.HubConnectionBuilder().withUrl('/votehub').build();
    
    let igoreNextVote = false;

    connection.on('ReceiveUpvote', () => {
        if (!igoreNextVote) {
            console.log('vote accepted');
            iotVotesObject.upvotes++;
            UpdateVoteCount(iotVotesObject);
        }else{
            console.log('vote ignored');
            igoreNextVote = false;
        }
    });

    connection.on('ReceiveDownvote', () => {
        if (!igoreNextVote) {
            iotVotesObject.downvotes++;
            UpdateVoteCount(iotVotesObject);
        }else{
            console.log('vote ignored');
            igoreNextVote = false;
        }
    });

    if (iot.isForm) {
        const voteAgree = document.createElement('button');
        voteAgree.className = 'btn-default mb-3';
        voteAgree.innerText = 'Stem voor';

        voteAgree.addEventListener('click',() => {
            igoreNextVote = true;
            voteForm(iotVotesObject,iot.formId,1);
            
            //connection.invoke('SendUpvote', IotLinkType.Form, iot.formId);
        });

        const voteDisAgree = document.createElement('button');
        voteDisAgree.className = 'btn-default mb-3';
        voteDisAgree.innerText = 'Stem tegen';

        voteDisAgree.addEventListener('click',() => {
            igoreNextVote = true;
            voteForm(iotVotesObject,iot.formId,0);
            
            //connection.invoke('SendDownvote', IotLinkType.Form, iot.formId);
        });

        const voteWrapper = document.createElement('div');
        voteWrapper.className = "vote-wrapper mt-2";
        voteWrapper.appendChild(voteAgree);
        voteWrapper.appendChild(voteDisAgree);
        feedbackWrapper.appendChild(voteWrapper);

        await connection.start();
        connection.invoke('JoinPage', iot.formId,IotLinkType.Form);

    }else{
        const openButton = document.createElement('a');
        openButton.className = 'btn-default mt-3 d-inline-block';
        openButton.setAttribute('href',`/ideation/view/${iot.ideationId}`);
        openButton.innerText = 'Bekijk en stem';
        feedbackWrapper.appendChild(openButton);


        await connection.start();
        connection.invoke('JoinPage', iot.ideationId,IotLinkType.Ideation);
    }

    UpdateVoteCount(iotVotesObject);

    iotCarousel.append(iotTCard);
    iotsLoaded++;
    if (iotsLoaded >= iotCount){
        document.getElementById('iot-loader').remove();
        ioTWrapper.style.opacity = '100';
    }

    const hasVoted = getCookie(`voted${iot.formId}`);
    if (hasVoted){
        setVoted(iotVotesObject);
    }
}

export async function LoadIoT(type,id) {
    let uri;
    if (type === "platform"){
        uri = `/api/iot/platform/all/${id}`;
    } else if(type === "project"){
        uri = `/api/iot/project/all/${id}`;
    }
    

    iotCount = document.getElementById('iot-count').value;

    await axios.get(uri)
        .then(async (r) => {
            const ioTWrapper = document.getElementById('iot-wrapper');

            iotCarousel = new Flickity(ioTWrapper,{
                pageDots: false
            });
            await r.data.forEach(ShowIoT);
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het ophalen van de iot opstellingen:');
            console.log(err);
        });
}