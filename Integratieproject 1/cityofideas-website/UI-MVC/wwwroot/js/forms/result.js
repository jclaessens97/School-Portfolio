import axios from "axios";
import {TypesEnum} from "../util";
import Chart from 'chart.js/dist/Chart.bundle.min'

let formId;
let questionsWrapper;

function addQuestion(result) {
    const question = result.question;
    const answers = result.answers;
    const wrapper = document.createElement('div');
    wrapper.className = 'col-lg-12 col-xl-6 mb-3';
    
    const card = document.createElement('div');
    card.className = 'card';
    const header = document.createElement('div');
    header.className = 'card-header';
    header.innerText = result.question.questionString;
    const body = document.createElement('div');
    body.className = 'card-body';

    card.appendChild(header);
    card.appendChild(body);


    let dynamicColors = function(opacity1,opacity2) {
        var r = Math.floor(Math.random() * 255);
        var g = Math.floor(Math.random() * 255);
        var b = Math.floor(Math.random() * 255);
        return ["rgba(" + r + "," + g + "," + b + "," + opacity1 + ")","rgba(" + r + "," + g + "," + b + "," + opacity2 + ")"];
    };
    
    
    switch (question.fieldType) {
        case TypesEnum.OPEN:
            body.setAttribute('style',"max-height: 250px; overflow-y:scroll;");
            const table = document.createElement('table');
            table.className = 'table table-sm';
            const thead = document.createElement('thead');
            table.appendChild(thead);
            const trMain = document.createElement('tr');
            thead.appendChild(trMain);
            
            const thIndex = document.createElement('th');
            trMain.appendChild(thIndex);
            thIndex.setAttribute('scope','col');
            thIndex.innerText = "#";
            
            const thAnswer = document.createElement('th');
            trMain.appendChild(thAnswer);
            body.setAttribute('scope','col');
            thAnswer.innerText = "Antwoord";
            
            const tBody = document.createElement('tbody');
            table.appendChild(tBody);
            
            let index = 1;
            answers.forEach((answer)=>{
                const tr = document.createElement('tr');
                tBody.appendChild(tr);
                const thIndexAnswer = document.createElement('th');
                thIndexAnswer.setAttribute('scope','row');
                thIndexAnswer.innerText = index;
                tr.appendChild(thIndexAnswer);
                const thAnswer = document.createElement('td');
                thAnswer.innerText = answer.value;
                tr.appendChild(thAnswer);
                index++;
            });
            body.appendChild(table);
            break;
        case TypesEnum.DROPDOWN:
        case TypesEnum.SINGLECHOICE:
        case TypesEnum.STATEMENT:
            const canvas2 = document.createElement('canvas');
            const ctx2 = canvas2.getContext('2d');

            let data2 = [];
            let j = 0;
            question.options.forEach(() => {
                let selected = 0;
                answers.forEach((answer) => {
                    const isSelected = answer.selectedChoice === j;
                    if (isSelected){
                        selected++;
                    }
                });
                data2.push(selected);
                j++;
            });

            let coloR2 = [];
            let colorBG2 = [];
            
            for (let j in data2) {
                const dynamicColor = dynamicColors(0.2,1);
                coloR2.push(dynamicColor[0]);
                colorBG2.push(dynamicColor[0]);
            }

            const dataSet = {
                datasets: [{
                    data: data2,
                    backgroundColor: coloR2,
                    borderColor: colorBG2,
                }],

                // These labels appear in the legend and in the tooltips when hovering different arcs
                labels: question.options
            };

            console.log(coloR2);
            console.log(colorBG2);
            const myChart = new Chart(ctx2, {
                type: 'doughnut',
                data: dataSet,
            });

            body.appendChild(canvas2);
            break;
        case TypesEnum.MULTIPLECHOICE:
            const canvas1 = document.createElement('canvas');
            const ctx1 = canvas1.getContext('2d');
            
            const data = [];
             
            let i = 0;
            question.options.forEach(() => {
                let selected = 0;
                answers.forEach((answer) => {
                    const isSelected = answer.selectedChoices[i];
                    if (isSelected){
                        selected++;
                    } 
                });
                data.push(selected);
                i++;
            });

            let coloR = [];
            let colorBG = [];

            

            for (let j in data) {
                const dynamicColor = dynamicColors(0.2,1);
                coloR.push(dynamicColor[0]);
                colorBG.push(dynamicColor[0]);
            }

            const chart = new Chart(ctx1, {
                type: 'bar',
                data: {
                    labels: question.options,
                    datasets: [{
                        label: 'Aantal stemmen',
                        data: data,
                        backgroundColor: coloR,
                        borderColor: colorBG,
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            });

            body.appendChild(canvas1);
            break;
        default:
            console.log('Missing field type');
    }

    wrapper.appendChild(card);
    questionsWrapper.appendChild(wrapper);
}

function loadFormData() {
    const uri = `/api/forms/results/${formId}`;
    
    
    axios.get(uri)
        .then((r) => {
            console.log(r.data);
            document.getElementById('form-loader').classList.add('d-none');
            document.getElementById('form-loader').classList.remove('d-block');
            r.data.forEach(addQuestion);
        }).catch((e) => {
            console.log('Er ging iets mis bij het ophalen van de form data');
            console.log(e);
    })
}

function init(){
    formId = document.getElementById("form-id").value;
    questionsWrapper = document.getElementById("questions-wrapper");
    loadFormData();
    
}

window.onload = init;