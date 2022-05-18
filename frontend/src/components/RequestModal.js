import React, {Component} from "react";
import PrimaryButton from "../components/PrimaryButton";
import { Link } from 'react-router-dom';
import UserContext from './UserContext';
import InactiveButton from "./InactiveButton";
import SearchForRandom from "./SearchForRandom";
import {conn} from "../App";

/*
    Component for the request modal that appears when users send game requests to each other
 */
class RequestModal extends Component {

    static contextType = UserContext;

    constructor(props) {
        super(props);
        this.state = {
            destLat: "",
            destLng: ""
        }

        this.getDestination = this.getDestination.bind(this)
        this.acceptRequest = this.acceptRequest.bind(this)
        this.declineRequest = this.declineRequest.bind(this)
        this.cancelRequest = this.cancelRequest.bind(this)
        this.startGame = this.startGame.bind(this)
    }

    // Queries the Google Places API to generate a random place in the currently selected map/city
    getDestination = () => new Promise((resolve, reject) => {
        // E.g. "Providence point of interest"
        let query = `${this.context.map} point of interest`
        let service = new window.google.maps.places.PlacesService(document.createElement('div'));
        let request = {
            query: query
        }
        // Uses the Google Places Text Search endpoint
        service.textSearch(request, (results, status) => {
            if (status === window.google.maps.places.PlacesServiceStatus.OK) {
                // Pick a random destination from the list of results
                let destination = results[Math.floor(Math.random() * results.length)]
                let lat = destination.geometry.location.lat()
                let lng = destination.geometry.location.lng()
                let picLink = ""
                // If the destination has a photo, include it as a link
                if (destination.photos) {
                    picLink = destination.photos[0].getUrl()
                }
                let destinationArr = [destination.name, [lat, lng], picLink]
                resolve(destinationArr)
            } else {
                reject("No destination found")
            }
        })
    })

    // Calculates a random start location for the players that's approximately 3/4 of a mile from the destination
    calculateStartLocations = (destLat, destLng) => new Promise((resolve, reject) => {
        // Calculates a random point about 3/4 of a mile away from the destination
        let r = 1200/111300 // 1200 meters = 0.746 miles
            , y0 = destLat
            , x0 = destLng
            , u = Math.random()
            , v = Math.random()
            , w = r * Math.sqrt(u)
            , t = 2 * Math.PI * v
            , x = w * Math.cos(t)
            , y1 = w * Math.sin(t)
            , x1 = x / Math.cos(y0)

        let startLat = y0 + y1
        let startLng = x0 + x1
        let start = new window.google.maps.LatLng(startLat, startLng)
        let service = new window.google.maps.StreetViewService()
        let request = {
            location: start,
            radius: 500,
            source: "outdoor",
            preference: "nearest"
        }
        // Get the closest panorama to the generated start locations
        service.getPanorama(request, (results, status) => {
            if (status === window.google.maps.StreetViewStatus.OK) {
                // Create an array for the start location with the panorama ID and location (lat, lng)
                let startArr = [results.location.pano, [results.location.latLng.lat(), results.location.latLng.lng()]]
                resolve(startArr)
            } else {
                // If we don't find a panorama in this range, calculate a new random start location
                this.calculateStartLocations(destLat, destLng).then(resolve)
            }
        })
    })

    // Sends a message to the backend to start a game given a gameID
    startGame = () => {

        console.log("start game")

        // sending a message to start the game
        const {opponent, gameId} = this.context;

        console.log("game id: " + gameId);

        const message = {
            "type": 7,
            "payload": {
                "gameId" : gameId,
                "player2" : opponent
            }
        }
        this.context.setRequestState("");
        conn.send(JSON.stringify(message));
    }

    // Generates destination and start location and then sends a message to create a new game in the backend
    acceptRequest = async () => {
        console.log("accepting")

        let destinationArr
        try {
            // Generate the destination for the players
            destinationArr = await this.getDestination()
        } catch(err) {
            console.log(err)
            return
        }
        let startArr
        try {
            // Generate the starting location for the players based on the destination
            startArr = await this.calculateStartLocations(destinationArr[1][0], destinationArr[1][1])
        } catch(err) {
            console.log(err)
            return
        }

        console.log("calculated destination -> sending accept message");

        const {opponent} = this.context;
        const message = { 
            "type": 3,
            "payload": {
                "player1" : opponent,
                "destination": destinationArr,
                "start": startArr,
                "location": this.context.location
            }
        }
        conn.send(JSON.stringify(message));

        const {setRequestState} = this.context;
        setRequestState("waiting");
    }

    // Declines a game request from a player
    declineRequest = () => {
        console.log("declining")
        const {opponent, setVisible, setRequestState} = this.context;
        setVisible(false);
        setRequestState("");

        const message = { 
            "type": 4,
            "payload": {
                "player1" : opponent
            }
        }
        conn.send(JSON.stringify(message));
    }

    // Cancels a request that the current player has sent
    cancelRequest = () => {
        console.log("cancelling")
        const {opponent, setVisible, setRequestState} = this.context;
        setVisible(false);
        setRequestState("");

        const message = { 
            "type": 9,
            "payload": {
                "player2" : opponent
            }
        }
        conn.send(JSON.stringify(message));
    }

    render() {

        const {popupVisible, opponent, requestState} = this.context;

        console.log(requestState)

        let requestString = "";
        let message = "";
        let buttons = "";

        // Depending on the set requestState, change the content of the request modal
        switch(requestState) {
            case "accepted":
                requestString = <h2>Game Request Accepted</h2>;
                message = <h4><b className="opponentName">{opponent}</b> has accepted your request!</h4>
                buttons = <Link to="/play" onClick={this.startGame}>
                    <PrimaryButton label="Play"/>
                </Link>
                break;
            case "sent":
                requestString = <h2>Game Request Sent</h2>;
                message = <h4>Waiting for <b className="opponentName">{opponent}</b> to respond...</h4>
                buttons = <div onClick={this.cancelRequest}><InactiveButton label="Cancel"/></div>
                break;
            case "received":
                requestString = <h2>Game Request</h2>;
                message = <h4><b className="opponentName">{opponent}</b> has requested to play with you in {this.context.location}</h4>
                buttons = <div ><InactiveButton label="Decline" onClick={this.declineRequest}/> <PrimaryButton label="Accept" onClick={this.acceptRequest}/></div>
                break;
            case "waiting":
                requestString = <h2>Game Request Accepted</h2>;
                message = <h4>Waiting for <b className="opponentName">{opponent}</b> to start the game...</h4>
                buttons = <div onClick={this.cancelRequest}><InactiveButton label="Cancel"/></div>
                break;
            case "randomSearch":
                break;
            default:
                this.context.setVisible(false);
                break;

        }

        // Set the popup content depending on the requestState switch statement above
        const popup = <div className="modal" style={{display: popupVisible ? "flex" : "none"}}>
            <div className="modalPopup popup">
                {requestString}
                {message}
                {buttons}
            </div>
        </div>;

        return ( requestState === "randomSearch" ? <SearchForRandom /> : popup) ;
    }
}

export default RequestModal;