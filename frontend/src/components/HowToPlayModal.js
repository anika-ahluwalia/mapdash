import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";

/*
    Component for the How to Play modal that can be opened from the select map or find opponent screens.
 */
function HowToPlayModal(props) {

    return (
        <div className="howToPlayModal">
            <FontAwesomeIcon className="closeIcon" icon={faTimes} onClick={props.click}/>
            <div className="howToPlayContentContainer">
                <h1 className="howToPlayTitle">How To Play</h1>
                <div className="howToPlayTextContainer">
                    <ol className="howToPlayList">
                        <li>
                            Once you load into a game, you and your opponent will be given the name of a destination
                            that you are racing to.
                        </li>
                        <li>
                            After the five-second countdown, you will have five minutes to navigate through the Google
                            StreetView and find your way to the destination.
                        </li>
                        <li>
                            You are given three hints, which you can use by clicking on the lightbulb button. These hints
                            will change your POV to point in the direction of the shortest path to the
                            destination. Remember to use all of your hints, they can only help you!
                        </li>
                        <li>
                            The game automatically ends when one player gets within a (insert number)
                            radius of the destination. If neither player finds the destination in time, the closest player
                            in terms of straight line distance to the destination wins!
                        </li>
                    </ol>
                </div>
            </div>
        </div>
    );
}

export default HowToPlayModal;