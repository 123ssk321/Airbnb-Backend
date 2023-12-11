'use strict';

/***
 * Exported functions to be used in the testing scripts.
 */
module.exports = {
	decideNextAction,
	genDeleteUserReply,
	genNewHouse,
	genNewHouseReply,
	genNewQuestion,
	genNewRental,
	genNewUser,
	genNewUserReply,
	genReply,
	genUpdateUsername,
	processUploadReply,
	selectHousesSkewed,
	selectImageToDownload,
	selectPeriod,
	selectQuestion,
	selectUserSkewed,
	random20,
	random50,
	random70,
	random80,
	random90,
	toJSON,
	uploadImageBody
}

/*----------------------------------------------- GLOBAL VARS AND CONST ----------------------------------------------*/

const { faker } = require('@faker-js/faker');
const fs = require('fs')
//const {c} = require("@faker-js/faker/dist/esm/chunk-5DGXOR5D.mjs");

let imagesIds = []
let images = []
let users = []
let houses = []

const locations = ["Lisbon","Porto","Madeira","Azores","Algarve","Braga","Coimbra","Evora","Aveiro","Leiria"]

// Auxiliary function to select an element from an array
Array.prototype.sample = function(){
	   return this[Math.floor(Math.random()*this.length)]
}

// Auxiliary function to select an element from an array
Array.prototype.sampleSkewed = function(){
	return this[randomSkewed(this.length)]
}

/*------------------------------------------------- EXPORTED FUNCTIONS -----------------------------------------------*/

/**
 * Decide next action
 * 0 -> browse popular
 * 1 -> browse recent
 */
function decideNextAction(context, events, done) {
	delete context.vars.auctionId;
	let rnd = Math.random()

	context.vars.nextAction = 1; // select location
	context.vars.location = locations.sample();
	let date = generateRandomDates(new Date(2023, 10, 1), new Date())
	context.vars.initDate = date[0].toLocaleDateString('en-CA');
	context.vars.endDate = date[1].toLocaleDateString('en-CA');

	if( rnd < 0.3)
		context.vars.afterNextAction = 0; // browsing
	else if( rnd < 0.4)
		context.vars.afterNextAction = 1; // check questions
	else if( rnd < 0.45) {
		context.vars.afterNextAction = 2; // post questions
		context.vars.text = `${faker.lorem.paragraph()}`;
	} else if( rnd < 0.60)
		context.vars.afterNextAction = 3; // reserve
	else
		context.vars.afterNextAction = 4; // do nothing
	return done()

}

function genDeleteUserReply(requestParams, response, context, ee, next) {
	if( response.statusCode >= 200 && response.statusCode < 300 && response.body.length > 0)  {
		let u = JSON.parse( response.body)
		const index = users.indexOf(u)
		console.log(u)
		users.splice(index, 1);
		fs.writeFileSync('users.data', JSON.stringify(users));

		//users.push(response.body)
		//fs.writeFileSync('users.data', JSON.stringify(users));
	}
	return next()
}

/**
 * Generate data for a new house using Faker
 */
function genNewHouse(context, events, done) {
	context.vars.name = `${faker.lorem.words({ min: 1, max: 3 })}`
	context.vars.location = locations.sample()
	context.vars.description = `${faker.lorem.paragraph()}`

	let cost
	let discount
	let dates
	let periods = []
	let startDate = new Date(2023, 11, 1)
	let n = random(10) + 1

	for(let i = 1; i <= n; i++){
		dates = generateRandomDates(startDate, new Date())
		startDate = dates[1]

		cost = random(500) + 200;
		discount = cost
		if(Math.random() < 0.5)
			discount = cost - random(5) * 10


		periods.push({
			startDate: dates[0].toLocaleDateString('en-CA'),
			endDate: dates[1].toLocaleDateString('en-CA'),
			price : cost,
			promotionPrice : discount,
			available : true,
		})
	}
	context.vars.periods = periods

	return done()
}

/**
 * Process reply for of new houses to store the id on file
 */
function genNewHouseReply(requestParams, response, context, ee, next) {
	if( response.statusCode >= 200 && response.statusCode < 300 && response.body.length > 0)  {
		let h = JSON.parse( response.body)
		houses.push(h)
		fs.writeFileSync('houses.data', JSON.stringify(houses));
		//houses.push(response.body)
		//fs.writeFileSync('houses.data', JSON.stringify(houses));
	}
	return next()
}

function genNewQuestion(context, events, done){
	context.vars.message = `${faker.lorem.paragraph()}`

	return done()
}

function genNewRental(context, events, done) {
	if(  houses.length > 0) {
		let house = houses.sampleSkewed()
		let periods = house.periods;

		let found = false
		let period

		for (const element of periods) {
			period = element

			if(period.available) {
				context.vars.period = period
				found = true

				context.vars.period = period
				context.vars.landlordId = house.ownerId
				context.vars.houseId = house.id

				break;
			}
		}

		if(!found){
			delete context.vars.period
			delete context.vars.landlordId
			delete context.vars.houseId
		}
	} else{
		delete context.vars.period
		delete context.vars.landlordId
		delete context.vars.houseId
	}

	return done()
}

/**
 * Generate data for a new user using Faker
 */
function genNewUser(context, events, done) {
	const first = `${faker.person.firstName()}`
	const last = `${faker.person.lastName()}`
	context.vars.id = first + "." + last
	context.vars.name = first + " " + last
	context.vars.pwd = `${faker.internet.password()}`
	return done()
}

/**
 * Process reply for of new users to store the id on file
 */
function genNewUserReply(requestParams, response, context, ee, next) {
	if( response.statusCode >= 200 && response.statusCode < 300 && response.body.length > 0)  {
		let u = JSON.parse( response.body)
		users.push(u)
		fs.writeFileSync('users.data', JSON.stringify(users));

		//users.push(response.body)
		//fs.writeFileSync('users.data', JSON.stringify(users));
	}
	return next()
}

function genReply(context, events, done){
	context.vars.message = `${faker.lorem.paragraph()}`
	context.vars.reply = `${faker.lorem.paragraph()}`

	context.vars.pwd =
		users.find(x => x.id === context.vars.ownerId).pwd;

	return done()
}

function genUpdateUsername(context, events, done) {
	if( users.length > 0) {
		// let user = users.sampleSkewed()
		// context.vars.id = user.id

		const first = `${faker.person.firstName()}`
		const last = `${faker.person.lastName()}`
		context.vars.nameToUpdate = first + " " + last

	} else {
		delete context.vars.id
		delete context.vars.name
	}
	return done()
}

/**
 * Process reply of the download of an image.
 * Update the next image to read.
 */
function processUploadReply(requestParams, response, context, ee, next) {
	if( typeof response.body !== 'undefined' && response.body.length > 0) {
		imagesIds.push(response.body)
	}
	return next()
}

function selectHousesSkewed(context, events, done) {
	if( houses.length > 0) {
		let house = houses.sampleSkewed()
		context.vars.houseId = house.id
		context.vars.ownerId = house.ownerId
	} else {
		delete context.vars.houseId
		delete context.vars.ownerId
	}
	return done()
}

/**
 * Select an image to download.
 */
function selectImageToDownload(context, events, done) {
	if( imagesIds.length > 0) {
		context.vars.imageId = imagesIds.sample()
	} else {
		delete context.vars.imageId
	}
	return done()
}

/**
 * Select rental from a list of rentals
 * assuming: user context.vars.user; rentals context.vars.rentalsLst
 */
function selectPeriod(context, events, done) {
	//delete context.vars.value;
	if( typeof context.vars.rentalLst !== 'undefined' &&
		context.vars.rentalLst.constructor === Array &&
		context.vars.rentalLst.length > 0) {
		let house = context.vars.rentalLst.sample()
		let period = house.periods.sample()

		if(period.available){
			let strRental = `{"id": null,
									"houseId": "${house.id}",
									 "tenantId":"${context.vars.id}",
									  "landlordId":"${houses.find((h) => h.id === house.id).ownerId}",
									   "period":{
									   		"startDate":"${period.startDate}", 
									   		"endDate":"${period.endDate}",
									   		"price":${period.price},
									   		"promotionPrice":${period.promotionPrice},
									   		"available":true}}`

			context.vars.rental = JSON.parse(strRental)
			context.vars.houseId = house.id
		} else {
			delete context.vars.rental
		}
	} else
		delete context.vars.rental
	return done()
}

/**
 * Select question from a list of question
 * assuming: user context.vars.user; questions context.vars.questionLst
 */
function selectQuestion(context, events, done) {
	delete context.vars.value;
	if( typeof context.vars.id !== 'undefined' && typeof context.vars.questionLst !== 'undefined' &&
		context.vars.questionLst.constructor === Array && context.vars.questionLst.length > 0) {
		let question = context.vars.questionLst.sample()

		context.vars.questionId = question.id;
		context.vars.owner = question.userId;
		context.vars.reply = `${faker.lorem.paragraph()}`;
	} else
		delete context.vars.questionId
	return done()
}

/**
 * Select user
 */
function selectUserSkewed(context, events, done) {
	if( users.length > 0) {
		let user = users.sampleSkewed()
		context.vars.id = user.id
		context.vars.pwd = user.pwd
	} else {
		delete context.vars.id
		delete context.vars.pwd
	}
	return done()
}

/**
 * Return true with probability 20%
 */
function random20(context, next) {
	const continueLooping = Math.random() < 0.2
	return next(continueLooping);
}

/**
 * Return true with probability 50%
 */
function random50(context, next) {
	const continueLooping = Math.random() < 0.5
	return next(continueLooping);
}

/**
 * Return true with probability 70%
 */
function random70(context, next) {
	const continueLooping = Math.random() < 0.7
	return next(continueLooping);
}

/**
 * Return true with probability 70%
 */
function random80(context, next) {
	const continueLooping = Math.random() < 0.8
	return next(continueLooping);
}

/**
 * Return true with probability 70%
 */
function random90(context, next) {
	const continueLooping = Math.random() < 0.9
	return next(continueLooping);
}

function toJSON(requestParams, response, context, ee, next){
	if( response.statusCode >= 200 && response.statusCode < 300){
		context.vars.sessionCookie = JSON.parse(context.vars.sessionId)
	}
	return next()
}

/**
 * Sets the body to an image, when using images.
 */
function uploadImageBody(requestParams, context, ee, next) {
	requestParams.body = images.sample()
	return next()
}

/*------------------------------------------------- UTILITY FUNCTIONS ------------------------------------------------*/

function generateRandomDates(from, to) {
	let startDate = new Date(
		from.getTime() +
		  Math.abs(Math.random() * (to.getTime() - from.getTime())),
	  )

	let endDate = new Date(startDate.getTime() + (1000 * 60 * 60 * 24) * random(20));
	
	return [startDate, endDate]
}

// Returns a random value, from 0 to val
function random( val){
	return Math.floor(Math.random() * val)
}

// Returns a random value, from 0 to val
function randomSkewed( val){
	let beta = Math.pow(Math.sin(Math.random()*Math.PI/2),2)
	let beta_left = (beta < 0.5) ? 2*beta : 2*(1-beta);
	return Math.floor(beta_left * val)
}

/**
 * Select house from a list of houses
 * assuming: user context.vars.user; houses context.vars.housesLst
 */
function selectHouse(context, events, done) {
	delete context.vars.value;
	if(  typeof context.vars.housesLst !== 'undefined' &&
		context.vars.housesLst.constructor === Array &&
		context.vars.housesLst.length > 0) {

		context.vars.houseId = context.vars.housesLst.sample();
	} else
		delete context.vars.houseId
	return done()
}

/*------------------------------------------------- SCRIPT ENTRYPOINT ------------------------------------------------*/

// Loads data about images from disk
function loadData() {
	let i
	let basefile
	if( fs.existsSync( '/images')) 
		basefile = '/images/house.'
	else
		basefile =  'images/house.'
	for( i = 1; i <= 50 ; i++) {
		let img  = fs.readFileSync(basefile + i + '.jpg')
		images.push( img)
	}
	let str;
	if( fs.existsSync('users.data')) {
		str = fs.readFileSync('users.data','utf8')
		users = JSON.parse(str)
	}
	if( fs.existsSync('houses.data')) {
		str = fs.readFileSync('houses.data','utf8')
		houses = JSON.parse(str)
	}
}

loadData();
