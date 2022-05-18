import React, {Component} from "react";
import UserContext from './UserContext';
import InactiveButton from "./InactiveButton";
import {conn} from "../App";
import {withRouter} from 'react-router-dom';
import PrimaryButton from "./PrimaryButton"

/*
    Component for the popup to be displayed when a game is over
 */
class GameOverPopup extends Component {

    static contextType = UserContext;

    constructor() {
        super();
        this.state = {
        }
        this.returnToHome = this.returnToHome.bind(this)
        this.sendRequest = this.sendRequest.bind(this)
    }

    // Resets context and returns to the select map screen
    returnToHome() {
        this.props.hideDenied();
        this.context.setOpponent("");
        this.context.setGameId(0);
        this.context.setRequestState("");
        this.context.setGameover(false);
        this.context.setRematch(false);
        const message = { 
            "type": 1,
            "payload": {
                "name" : this.context.nickname
            }
        }
        conn.send(JSON.stringify(message));

        this.props.history.push('/opponent');
    }

    // Called for rematch, hides modal and sends a new request to opponent
    sendRequest() {
        this.props.hideDenied();
        const {setVisible, setRequestState} = this.context;
        setRequestState("sent");
        setVisible(true);
        this.context.setRematch(true);
        const message = { 
            "type": 2,
            "payload": {
                "player2" : this.context.opponent,
                "location" : this.context.location
            }
        }
        conn.send(JSON.stringify(message));
    }
    

    render() {

        return <div className="gameoverPopup" style={{display: this.context.gameover ? "flex" : "none"}}>
            <div className="gameoverPopup-content popup">
                <h2>Game Over!</h2>
                <h4><b className="opponentName">{this.context.leader}</b> won! </h4>
                <div>
                    <InactiveButton label="Return to Home" onClick={this.returnToHome}/> 
                    {!this.props.disconnect ? <PrimaryButton label="Rematch" onClick={this.sendRequest}/> : <></>}
                </div>
            </div>
        </div>;
    }
}

export default withRouter(GameOverPopup);