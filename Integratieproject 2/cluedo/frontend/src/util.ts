export default class Utils {
  static getRandomNumberBetween(min: number, max: number) {
    return Math.floor((Math.random() * max) + min);
  }
}
