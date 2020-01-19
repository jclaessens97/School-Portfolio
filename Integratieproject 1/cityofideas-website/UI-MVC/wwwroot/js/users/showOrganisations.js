import axios from 'axios';
import $ from 'jquery';
import * as toastr from 'toastr';

const baseUri = '/api/users';
const claims = [
    'Admin',
    'User',
    'Moderator'
];

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
        await axios.post(postUrl, JSON.stringify(data), { headers });
        row.remove();
        toastr.clear();
        toastr.success('Organisatie '+data.text+' sucessvol verwijdert.');
    } catch (err) {
        toastr.clear();
        toastr.error('Er ging iets mis bij het verwijderen van organisatie'+data.text+'.');
        console.error(err);
    }
}

async function deleteUser(val) {
    console.log(val.username);
    toastr.warning("<br/><button type='button' id='confirmationDeleteYes' class='btn clear'>Ja</button><button type='button' id='confirmationDeleteNo' class='btn clear'>Nee</button>", `Bent u zeker van het verwijderen van organisatie ${val.username}?`,
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
    const postUrl = `${baseUri}/lockuser?username=${val}&duration=${duration}`;

    try {
        await axios.post(postUrl);
        toastr.clear();
        toastr.success('Organisatie sucessvol ge(de)blokkeerd');
    } catch (err) {
        toastr.clear();
        toastr.error('Er ging iets mis bij het blokkeren van de organisatie');
        console.error(err);
    }
}

async function getUsers() {
    try {
        const response = await axios.get('/api/users');
        return response.data;
    } catch (err) {
        toastr.error(err);
        // alert(err);
        return [];
    }
}

async function loadUsers() {
    const users = await getUsers();
    let count = 0;
    console.log(users.list);
    
    users.list.forEach((user) => {
        if (user.organisation) {
            count++;
            const rows = document.createElement('tr');
            rows.id = user.username;
            rows.className = 'Search';

            // var username = user.username.split('_');

            const firmnameColumn = document.createElement('td');
            firmnameColumn.id = 'Firmname';
            firmnameColumn.innerText = user.firmName;
            rows.appendChild(firmnameColumn);
    
            // const lastnameColumn = document.createElement('td');
            // lastnameColumn.id = 'Lastname';
            // lastnameColumn.innerText = username[1].toLowerCase();
            // rows.appendChild(lastnameColumn);

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
            button.className='btn btn-block btn-primary btn-flat';
            button.style.width="50px";
            button.style.backgroundColor = "#3C8DBC";
            button.style.borderColor = "#3C8DBC";
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
            blockColumn.setAttribute("align","center");
            const input = document.createElement('input');
            input.setAttribute('type', 'checkbox');
            input.className="";
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
    if (count === 0) {
        toastr.info('Op dit moment zijn er geen organisaties in dit project.');
    }
}

function addEventHandlers() {
    $('#Search').keyup(() => {
        const searchText = $('#Search').val().toLowerCase();
        $('.Search').each(() => {
            if (!$(this.firstChild).text().toLowerCase().includes(searchText)) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    });
}

function init() {
    loadUsers();
    addEventHandlers();
}

window.onload = init;
