import { faTimesCircle } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import UserContext from './UserContext';
import React, {Component} from "react";
import {conn} from "../App";

/*
    Component for the background overlay that appears when a user is searching for a random opponent
 */
class SearchForRandom extends Component {

    static contextType = UserContext;

    constructor() {
        super();
        this.state = {
        }
        this.cancelRandom = this.cancelRandom.bind(this)
    }

    // Cancels a request that the current player has sent in random mode
    cancelRandom = () => {
        this.context.setVisible(false)
        console.log("cancelling")
        this.context.setRequestState("");

        const message = { 
            "type": 9,
            "payload": {
                "player2" : this.context.opponent
            }
        }
        conn.send(JSON.stringify(message));
    }

    render() {
        const {popupVisible, setVisible} = this.context

        return (<div className="randomOverlay" style={{display: popupVisible ? "flex" : "none"}}>
                <div className="randomOverlay-content">
                    <h1 className="headerText">Searching for an Opponent...</h1>
                    <FontAwesomeIcon className="exitIcon" icon={faTimesCircle} 
                        onClick={this.cancelRandom}/>
                </div>
            </div>
        );
    }
}

export default SearchForRandom;