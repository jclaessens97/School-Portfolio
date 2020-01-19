import axios from 'axios';
import $ from 'jquery';
import * as toastr from 'toastr';

const baseUri = '/api/users';
const claims = [
    'Admin',
    'User',
    'Moderator'
];

var subdomain;
if (window.location.hostname.split(".")[0] !== "localhost" || window.location.hostname.split(".")[0] !== "cityofidea") {
    subdomain = window.location.hostname.split(".")[0];
}

async function submitUsernameAndClaim(val) {
    const data = {
        text: `${val.username}|${val.claim}`,
    };
    const postUrl = `${baseUri}/changeclaim`;
    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        await axios.post(postUrl, JSON.stringify(data), {headers});
        toastr.clear();
        toastr.success('Sucessvol claim gewijzigd');
    } catch (err) {
        toastr.clear();
        toastr.error('Er ging iets mis bij het wijzigen van de claim.')
    }
}

async function deleteUserTrue(val) {
    const data = {
        text: val.username,
    };
    console.log(data.text);

    const postUrl = `${baseUri}/deleteuser`;
    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        const row = document.getElementById(data.text);
        console.log(row);
        await axios.post(postUrl, JSON.stringify(data), {headers});
        row.remove();
        toastr.clear();
        toastr.success('Gebruiker ' + data.text + ' sucessvol verwijdert.');
    } catch (err) {
        toastr.clear();
        toastr.error('Er ging iets mis bij het verwijderen van gebruiker ' + data.text + '.');
        console.error(err);
    }
}

async function deleteUser(val) {
    console.log(val.username);
    toastr.clear();
    toastr.warning("<br/><button type='button' id='confirmationDeleteYes' class='btn clear'>Ja</button><button type='button' id='confirmationDeleteNo' class='btn clear'>Nee</button>", `Bent u zeker van het verwijderen van gebruiker ${val.username}?`,
        {
            closeButton: false,
            allowHtml: true,
            onShown() {
                $('#confirmationDeleteYes').click(() => {
                    deleteUserTrue(val);
                    console.log('clicked yes');
                });
                $('#confirmationDeleteNo').click(() => {
                    console.log('clicked no');
                });
            },
        });
}

async function blockUser(val, duration) {
    // Heb hier een verandering gedaan waardoor blokkeren van een user via een querystring gebeurt
    const postUrl = `${baseUri}/lockuser?username=${val}&duration=${duration}`;

    try {
        await axios.post(postUrl);
        toastr.clear();
        toastr.success('Sucessvol gebruiker ge(de)blokkeerd');
    } catch (err) {
        toastr.clear();
        toastr.error('Er ging iets mis bij het blokkeren van de gebruiker');
        console.error(err);
    }
}

async function getUsers() {
    try {
        const response = await axios.get('/api/users');
        return response.data;
    } catch (err) {
        toastr.error(err);
        return [];
    }
}

async function loadUsers() {
    console.log(subdomain);
    const users = await getUsers();
    console.log("users:"+users);
    const currUser = users.userClaim;
    //TODO 
    let superadmin = true;
    /*currUser.forEach((claim) => {
        if (claim.type === 'SuperAdmin') {
            superadmin = true;  
        }
    });*/
    
    // if (superadmin)
        users.list.forEach((user) => {
            if (user.organisation){
                console.log(`organisatie ${user.username} wordt niet weergegeven.`);
            } else if (user.currentClaim.includes('SuperAdmin')) {
                const rows = document.createElement('tr');
                rows.id = user.username;
                rows.className = 'Search';

                const firstnameColumn = document.createElement('td');
                firstnameColumn.id = 'Firstname';
                firstnameColumn.innerText = user.firstName;
                rows.appendChild(firstnameColumn);

                const lastnameColumn = document.createElement('td');
                lastnameColumn.id = 'Lastname';
                lastnameColumn.innerText = user.lastName;
                rows.appendChild(lastnameColumn);

                const emailColumn = document.createElement('td');
                emailColumn.id = 'Email';
                emailColumn.innerText = user.email;
                rows.appendChild(emailColumn);

                const permissionsColumn = document.createElement('td');
                permissionsColumn.id = 'Permissions';
                permissionsColumn.innerText = 'SuperAdmin';
                rows.appendChild(permissionsColumn);

                const deleteColumn = document.createElement('td');
                deleteColumn.id = 'Delete';
                deleteColumn.innerText = 'SuperAdmin';
                rows.appendChild(deleteColumn);

                const blockColumn = document.createElement('td');
                blockColumn.id = 'Block';
                blockColumn.innerText = 'SuperAdmin';
                rows.appendChild(blockColumn);

                const table = document.getElementById('Table');
                table.appendChild(rows);
            } else {
                const rows = document.createElement('tr');
                rows.className = 'Search';
                rows.id = user.username;

                const firstnameColumn = document.createElement('td');
                firstnameColumn.id = 'Firstname';
                firstnameColumn.innerText = user.firstName;
                rows.appendChild(firstnameColumn);

                const lastnameColumn = document.createElement('td');
                lastnameColumn.id = 'Lastname';
                lastnameColumn.innerText = user.lastName;
                rows.appendChild(lastnameColumn);

                const emailColumn = document.createElement('td');
                emailColumn.id = 'Email';
                emailColumn.innerText = user.email;
                rows.appendChild(emailColumn);

                const permissionsColumn = document.createElement('td');
                permissionsColumn.id = 'Permissions';
                permissionsColumn.setAttribute("align","center");
                const select = document.createElement('select');
                select.className = "form-control";
                claims.forEach((claim) => {
                    const option = document.createElement('option');
                    option.value = claim;
                    option.innerText = claim;
                    select.appendChild(option);
                });
                select.value = user.currentClaim;
                select.addEventListener('change', (event) => {
                    const userObj = {
                        username: user.username,
                        claim: event.target.value,
                    };

                    submitUsernameAndClaim(userObj);
                });
                permissionsColumn.appendChild(select);
                rows.appendChild(permissionsColumn);

                const deleteColumn = document.createElement('td');
                deleteColumn.id = 'Delete';
                deleteColumn.setAttribute("align","center");
                const button = document.createElement('button');
                button.className = 'btn btn-block btn-primary btn-flat';
                button.style.width = "50px";
                button.style.backgroundColor = "#3C8DBC";
                button.style.borderColor = "#3C8DBC";
                const deleteImage = document.createElement('i');
                deleteImage.className = "far fa-trash-alt";

                button.appendChild(deleteImage);
                button.addEventListener('click', () => {
                    const deleteObj = {
                        username: user.username,
                    };
                    deleteUser(deleteObj);
                });
                deleteColumn.appendChild(button);
                rows.appendChild(deleteColumn);

                const blockColumn = document.createElement('td');
                blockColumn.id = 'Block';
                blockColumn.setAttribute("align","center");
                const input = document.createElement('input');
                input.setAttribute('type', 'checkbox');
                input.className = "";
                input.checked = user.lockOutEnabled;
                input.addEventListener('click', () => {
                    if (input.checked) {
                        blockUser(user.username, 5);
                    } else {
                        blockUser(user.username, -2); // duration van -2 = unblocken van user, duration van -1, permanent blocken (bij vragen vraag aan mij (arne))
                    }
                });
                blockColumn.appendChild(input);
                rows.appendChild(blockColumn);

                const table = document.getElementById('Table');
                table.appendChild(rows);
            }
        });
    /*} else {
        users.list.forEach((user) => {
            console.log(`claim user: ${user.currentClaim}`);
            if (user.currentClaim.includes('Organisation') || user.currentClaim.includes('Admin') || user.currentClaim.includes('SuperAdmin')) {
                console.log(`organisatie/admin/superadmin ${user.username} wordt niet weergegeven.`);
            } else {
                console.log(user.currentClaim);
                const rows = document.createElement('tr');
                rows.className = 'Search';
                rows.id = user.username;

                /!*const usernameColumn = document.createElement('td');
                usernameColumn.id = 'Username';
                usernameColumn.innerText = user.username;
                rows.appendChild(usernameColumn);*!/
                
                const firstnameColumn = document.createElement('td');
                firstnameColumn.id = 'Firstname';
                firstnameColumn.innerText = user.firstname;
                rows.appendChild(firstnameColumn);

                const lastnameColumn = document.createElement('td');
                lastnameColumn.id = 'Lastname';
                lastnameColumn.innerText = user.lastname;
                rows.appendChild(lastnameColumn);
                const emailColumn = document.createElement('td');
                emailColumn.id = 'Email';
                emailColumn.innerText = user.email;
                rows.appendChild(emailColumn);

                const permissionsColumn = document.createElement('td');
                permissionsColumn.id = 'Permissions';
                const select = document.createElement('select');
                claims.forEach((claim) => {
                    const option = document.createElement('option');
                    option.value = claim;
                    option.innerText = claim;
                    select.appendChild(option);
                });
                select.value = user.currentClaim;
                select.addEventListener('change', (event) => {
                    const userObj = {
                        username: user.username,
                        claim: event.target.value,
                    };

                    submitUsernameAndClaim(userObj);
                });
                permissionsColumn.appendChild(select);
                rows.appendChild(permissionsColumn);

                const deleteColumn = document.createElement('td');
                deleteColumn.id = 'Delete';
                const button = document.createElement('button');
                const deleteImage = document.createElement('i');
                deleteImage.className = 'far fa-trash-alt';
                button.appendChild(deleteImage);
                button.addEventListener('click', () => {
                    const deleteObj = {
                        username: user.username,
                    };
                    deleteUser(deleteObj);
                });
                deleteColumn.appendChild(button);
                rows.appendChild(deleteColumn);

                const blockColumn = document.createElement('td');
                blockColumn.id = 'Block';
                const input = document.createElement('input');
                input.setAttribute('type', 'checkbox');
                input.checked = user.lockOutEnabled;
                blockColumn.innerText = 'Blocked?\t';
                input.addEventListener('click', () => {
                    blockUser(user.username);
                });
                blockColumn.appendChild(input);
                rows.appendChild(blockColumn);

                const table = document.getElementById('Table');
                table.appendChild(rows);
            }
        });
    }*/
}

function addEventHandlers() {
    // TODO: make work without jquery
    $('#Search').keyup(async () => {
        const search = $('#Search').val().toUpperCase();
        console.log(search);
        const response = await axios.get(`/api/users/${search}`);
        console.log(response.data.list);
        $('#Search').autocomplete({source: response.data.list});
    });
}

function init() {
    loadUsers();
    addEventHandlers();
}

window.onload = init;
