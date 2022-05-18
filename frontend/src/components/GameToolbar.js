import React, {Component} from "react";
import {Link} from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faLightbulb} from "@fortawesome/free-solid-svg-icons/faLightbulb";
import logo from "../assets/logo.svg";
import Countdown, {zeroPad} from "react-countdown";
import UserContext from './UserContext';

/*
    Component for the toolbar that appears at the top of the game screen. Contains info on the destination,
    which player is in the lead, how many hints a player has, and the time left.
 */
class GameToolbar extends Component {

    static contextType = UserContext;

    constructor() {
        super();
        this.state = {
            // Countdown set to 5 minutes
            // date: Date.now() + 300000,
            date: Date.now() + 10000,
        }
    }

    // Renderer for the Countdown component
    renderer = ({minutes, seconds, completed }) => {
        // If the countdown is over, send a game over message and change text
        if (completed) {
            this.context.setInGame(false);
            if (!this.context.gameover && !this.context.rematch) {
                this.props.onGameOver();
            }
            return <div className="toolbarLargeLabel">Game Over!</div>
        } else {
            // Render a countdown
            this.context.setInGame(true);
            this.context.setRematch(false);
            return <span>{minutes}:{zeroPad(seconds)}</span>;
        }
    };

    render() {
        const { refCallback } = this.props;
        const { hints } = this.props
        const { leader } = this.context

        return (
            <div className="toolbarContainer">
                <div className="toolbarLogoContainer">
                    <img src={logo} className="toolbarLogo" alt="MapDash Logo"/>
                </div>
                <div className="toolbarDestContainer">
                    <p className="toolbarLabel">Destination</p>
                    <p className="toolbarLargeLabel">{this.context.destination[0]}</p>
                </div>
                <div className="toolbarRightHalfContainer">
                    <p className="toolbarLabel">In the Lead</p>
                    <p className="toolbarLargeLabel">{leader}</p>
                </div>
                <div className="toolbarRightHalfContainer">
                    <p className="toolbarLabel">Hints</p>
                    <div>
                        {hints > 2 ? <FontAwesomeIcon className="hintIcon" icon={faLightbulb}/>: <FontAwesomeIcon className="usedHint hintIcon" icon={faLightbulb}/>}
                        {hints > 1 ? <FontAwesomeIcon className="hintIcon" icon={faLightbulb}/>: <FontAwesomeIcon className="usedHint hintIcon" icon={faLightbulb}/>}
                        {hints > 0 ? <FontAwesomeIcon className="hintIcon" icon={faLightbulb}/>: <FontAwesomeIcon className="usedHint hintIcon" icon={faLightbulb}/>}
                    </div>
                </div>
                <div className="toolbarRightHalfContainer">
                    <p className="toolbarLabel">Time Left</p>
                    <Countdown
                        ref={refCallback}
                        autoStart={false}
                        renderer={this.renderer}
                        date={this.state.date}
                    />
                </div>
            </div>
        );
    }
}

export default GameToolbar;