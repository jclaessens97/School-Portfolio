import counterUp from 'counterup2';

const counterEl = document.querySelector('.counter');

if (counterEl !== null) {
    counterUp(counterEl, {
        duration: 500,
        delay: 16,
    });
}
