import axios from 'axios';
import $ from 'jquery';
import * as toastr from 'toastr';

function loadShowBar() {
    var searchForm = document.getElementById("searchForm");
    var searchLink = document.createElement("a");
    searchLink.id = "searchLink";
    var questionmark = document.createElement("i");
    questionmark.className="fas fa-search";
    searchLink.appendChild(questionmark);
    var searchText = document.createElement("input");
    searchText.style.visibility = "hidden";
    searchText.setAttribute("type", "text");
    searchForm.appendChild(searchText);
    searchForm.appendChild(searchLink);
    searchLink.addEventListener("click", (event) => {
        searchText.style.visibility = "visible";
        searchText.placeholder = "Zoeken";
    });

   
}


function init() {
    loadShowBar();
}

window.onload = init;