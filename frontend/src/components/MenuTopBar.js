import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import logo from "../assets/logo.svg";
import { Link } from 'react-router-dom';
import { faInfoCircle, faMap } from "@fortawesome/free-solid-svg-icons";
import UserContext from './UserContext';

/*
    Component for the header section of the select map and find opponent screens. Displays the user's current
    nickname, selected map, option to go back to the select map screen and change it, and the button to display
    the how to play modal.
 */
class MenuTopBar extends React.Component {

    static contextType = UserContext;

    render() {
        const {nickname, map, setMap} = this.context

        return (
            <div className="menuTopBar">
                <img src={logo} className="topLogo" alt="MapDash Logo"/>
                <div className="topInfo-wrapper">
                    <div className="topInfo">Nickname: <b className="accent">{nickname}</b></div>
                    {(this.props.displayMap && map !== "") &&
                        <div className="topInfo-map-wrapper">
                            <div className="topInfo">Map: <b className="accent">{map}</b></div>
                        </div>
                    }
                    <Link to="/selectmap" onClick={() => {setMap("")}}>
                        <FontAwesomeIcon className="infoIcon" icon={faMap}/>
                    </Link>
                    <FontAwesomeIcon className="infoIcon" icon={faInfoCircle} onClick={this.props.infoClick}/>
                </div>
            </div>
        );
    }
}

export default MenuTopBar;