import React, {Component} from "react";
import PrimaryButton from "../components/PrimaryButton";
import { Link } from 'react-router-dom';
import MenuTopBar from "../components/MenuTopBar";
import Dropdown from "../components/Dropdown";
import InactiveButton from "../components/InactiveButton";
import UserContext from "../components/UserContext";
import HowToPlayModal from "../components/HowToPlayModal";
import {conn} from "../App";
import {withRouter} from 'react-router-dom';
import SocketErrorPopup from "../components/SocketErrorPopup";

/*
    Screen that allows a user to select a map to play on
 */
class SelectMapScreen extends Component {

    static contextType = UserContext;

    constructor() {
        super();
        this.state = {
            map: "",
            showHowToPlay: false,
            socketError: false
        }
        this.updateMap = this.updateMap.bind(this)
        this.toggleHowToPlayModal = this.toggleHowToPlayModal.bind(this)
        this.sendMap = this.sendMap.bind(this)
        this.toggleErrorPopup = this.toggleErrorPopup.bind(this)
    }

    componentDidMount(){
        const { nickname, setRequestState, setOpponent, setVisible, setOpponentsList } = this.context;

        if (nickname === "") {
            this.props.history.push('/');
            return;
        }

        // Set up web socket connection listening
        conn.onmessage = (event) => {
            const msg = JSON.parse(event.data);

            console.log(msg);

            switch(msg.type) {
            // REQUEST message: listen for a request from a different player
            case 2:
                setRequestState("received")
                setOpponent(msg.payload.player1)
                this.context.setLocation(msg.payload.location)
                setVisible(true)
                break;
            // ACCEPT message: accept a request
            case 3:
                // Set the destination name and location to be used for the game toolbar and reference picture
                this.context.setDestination(msg.payload.destination)
                this.context.setLocation(msg.payload.location)
                // Set the start panorama ID and location for StreetView
                this.context.setStart(msg.payload.start)
                this.context.setGameId(msg.payload.gameId);

                if (msg.payload.playerNumber === 1) {
                    setRequestState("accepted")
                    setVisible(true)
                }
                break;
            // DECLINE message: decline a request
            case 4:
                setRequestState("declined")
                setVisible(false)
                break;
            // ACTIVE message: update active players list
            case 5:
                const players = msg.payload.players.replace(/['"]+/g, '').replace("[", '').replace("]", '');
                const listPlayers = players.split(",")
                setOpponentsList(listPlayers.filter(name => name !== nickname));
                break;
            // PLAY message: start game
            case 7:
                this.context.setRequestState("")
                this.context.setGameover(false);
                this.props.history.push('/play');
                break;
            // CANCEL message: cancel a request (only possible from player who sent it)
            case 9:
                // cancelled request
                setRequestState("")
                setVisible(false)
                break;
            // ERROR message: tells the frontend that the backend received some web socket error
            case 13:
                this.setState({socketError: true})
                break
            default:
                break;
            }
        }
        this.context.setInGame(false);
        setVisible(false);
    }

    // Toggles the visibility of the How To Play modal
    toggleHowToPlayModal() {
        this.setState({showHowToPlay: !this.state.showHowToPlay})
    }

    // Toggles the visibility of a socket error popup
    toggleErrorPopup() {
        this.setState({socketError: !this.state.socketError})
    }

    // Updates the currently selected map in context
    updateMap(value) {
        const {setMap} = this.context
        this.setState({map: value})
        setMap(value);
        this.context.setLocation(value)
    }

    // Send a message to the backend to set the map
    sendMap() {
        const message = { 
            "type": 6,
            "payload": {
                "map" : this.state.map
            }
        }
        conn.send(JSON.stringify(message));
    }

    render() {
        // List of supported maps
        //const locations = ["Providence", "New York", "Los Angeles", "Chicago", "Miami", "Philadelphia", "Seattle"];
        const locations = ["Providence"];
        
        return (
            <div className="landingContainer">
                <MenuTopBar displayMap={false} infoClick={this.toggleHowToPlayModal}/>
                {this.state.showHowToPlay &&
                    <HowToPlayModal click={this.toggleHowToPlayModal}/>
                }
                <div className="middleContentContainer">
                    <h1 className="headerText">Select a Map</h1>
                    <Dropdown options={locations} select={this.context.setMap}/>
                    {this.context.map === "" ? <InactiveButton label="Next"/> :
                        <Link to="/opponent" onClick={this.sendMap}>
                            <PrimaryButton label="Next"/>
                        </Link>
                    }
                </div>
                {this.state.socketError &&
                    <SocketErrorPopup click={this.toggleErrorPopup}/>
                }
            </div>
        );
    }
}

export default withRouter(SelectMapScreen);