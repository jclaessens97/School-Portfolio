import axios from 'axios';

async function loadPlatforms() {
    try {
        const response = await axios.get(`/api/platforms/${0}/${5}`);

        const list = document.getElementById('platform-results');

        response.data.forEach((platform) => {
            const parent = document.createElement('div');
            parent.className = 'row platform-row align-items-center mt-4';

            const imgContainer = document.createElement('div');
            imgContainer.className = 'col-md-2 text-center';

            const logo = document.createElement('img');
            logo.src = platform.logo.url;
            logo.className = 'platform-logo img-fluid';
            logo.alt = platform.logo.name;

            const descContainer = document.createElement('div');
            descContainer.className = 'col-md-10';

            const h3 = document.createElement('h3');
            h3.innerText = platform.name;

            const p = document.createElement('p');
            p.innerText = platform.description;

            const a = document.createElement('a');
            a.href = platform.tenant;
            a.className = 'btn btn-default float-right homepage-platform-button';

            const span = document.createElement('span');
            span.innerText = `Ga naar ${platform.name}`;

            a.appendChild(span);

            descContainer.append(h3);
            descContainer.append(p);
            descContainer.append(a);

            imgContainer.appendChild(logo);

            parent.appendChild(imgContainer);
            parent.appendChild(descContainer);

            list.appendChild(parent);
        });
    } catch (err) {
        console.error(err);
    }
}

window.onload = loadPlatforms;