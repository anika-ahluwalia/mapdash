import React, {Component} from "react";
import PrimaryButton from "../components/PrimaryButton";
import MenuTopBar from "../components/MenuTopBar";
import Dropdown from "../components/Dropdown";
import InactiveButton from "../components/InactiveButton";
import HowToPlayModal from "../components/HowToPlayModal";
import ToggleBar from "../components/ToggleBar";
import UserContext from '../components/UserContext';
import {conn} from "../App";
import {withRouter} from 'react-router-dom';
import InvalidPopup from "../components/InvalidPopup";
import SocketErrorPopup from "../components/SocketErrorPopup";

/*
    Screen to find an opponent, either through random queueing or selecting someone from the active players list
 */
class FindOpponentScreen extends Component {

    static contextType = UserContext;

    constructor() {
        super();
        this.state = {
            mode: "Random",
            showHowToPlay: false,
            invalidVisible: false,
            invalidMessage: "",
            socketError: false,
        }
        this.updateOpponent = this.updateOpponent.bind(this)
        this.updateMode = this.updateMode.bind(this)
        this.sendRequest = this.sendRequest.bind(this)
        this.getRandomOpponent = this.getRandomOpponent.bind(this)
        this.toggleHowToPlayModal = this.toggleHowToPlayModal.bind(this)
        this.hideDenied = this.hideDenied.bind(this)
        this.toggleErrorPopup = this.toggleErrorPopup.bind(this)
        this.showEmptyMapWarning = this.showEmptyMapWarning.bind(this)
    }

    componentDidMount() {
        const { nickname, setRequestState, setOpponent, setVisible, setOpponentsList, setGameId} = this.context;

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
                setGameId(msg.payload.gameId);

                if (msg.payload.playerNumber === 1) {
                    setRequestState("accepted")
                    setVisible(true)
                }
                break;
            // DECLINE message: decline a request
            case 4:
                setRequestState("declined")
                setVisible(false)
                this.setState({invalidMessage: "Request Denied. Please try again", invalidVisible: true})
                setTimeout(() => this.setState({invalidMessage: "", invalidVisible: false}), 3000)
                break;
            // ACTIVE message: update active players list
            case 5:
                console.log("received players");
                console.log(msg.payload.players);
                const players = msg.payload.players.replace(/['"]+/g, '').replace("[", '').replace("]", '');
                const listPlayers = players.split(",")
                console.log(listPlayers);
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
                console.log("cancelling");
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
        this.hideDenied();
        this.setState({showHowToPlay: !this.state.showHowToPlay})
    }

    // Toggles the visibility of a socket error popup
    toggleErrorPopup() {
        this.setState({socketError: !this.state.socketError})
    }

    // Hides the denied request message popup
    hideDenied() {
        this.setState({invalidVisible: false})
    }

    // Update the current opponent for a player
    updateOpponent(value) {
        this.hideDenied();
        const {setOpponent} = this.context;
        setOpponent(value);
        this.setState({opponent: value})
    }

    // Update the mode of opponent finding selected
    updateMode(value) {
        this.setState({mode: value})
        this.hideDenied();
    }

    showEmptyMapWarning() {
        this.setState({invalidMessage: "You don't have a map selected. Click the map button in the toolbar to choose a map!",
            invalidVisible: true})
        setTimeout(() => this.setState({invalidMessage: "", invalidVisible: false}), 3000)
    }

    // Send a request to an opponent via a message to the backend and display a sent pop-up on the current player's side
    sendRequest() {
        this.hideDenied();
        const {setVisible, setRequestState} = this.context;
        setRequestState("sent");
        setVisible(true);

        const message = { 
            "type": 2,
            "payload": {
                "player2" : this.context.opponent,
                "location" : this.context.map
            }
        }
        conn.send(JSON.stringify(message));
    }

    // Search for a random opponent from the list of active opponents, then send a request message to the backend
    getRandomOpponent() {
        this.hideDenied();
        const {setVisible, setRequestState, opponentsList, setOpponent} = this.context;
        setRequestState("randomSearch");
        setVisible(true);

        if (opponentsList.length === 0) {
            setVisible(false)
            this.setState({invalidMessage: "No online players. Try again later."})
            this.setState({invalidVisible: true})
        } else {
            const opponent = opponentsList[Math.floor(Math.random() * opponentsList.length)];
            setOpponent(opponent)
            // this.setState({opponent: opponent})

            const message = { 
                "type": 2,
                "payload": {
                    "player2" : opponent,
                    "location" : this.context.map
                }
            }
            conn.send(JSON.stringify(message));
        }
    }

    render() {
        // UI for the friend dropdown content
        const friend =
            <>
                <Dropdown options={this.context.opponentsList} select={this.updateOpponent}/>
                {(this.context.opponent === "" || this.context.map === "")
                    ? <InactiveButton onClick={this.showEmptyMapWarning} label="Send Request"/>
                    : <PrimaryButton label="Send Request" onClick={this.sendRequest}/>
                }
            </>

        // UI for the random opponent content
        const random =
            <>
                {this.context.map === ""
                    ? <InactiveButton onClick={this.showEmptyMapWarning} label="Queue and Play!"/>
                    : <PrimaryButton label="Queue and Play!" onClick={this.getRandomOpponent}/>
                }

            </>

        return (
            <div className="landingContainer">
                <MenuTopBar displayMap={true} infoClick={this.toggleHowToPlayModal}/>
                {this.state.showHowToPlay &&
                    <HowToPlayModal click={this.toggleHowToPlayModal}/>
                }
                <div className="middleContentContainer">
                    <h1 className="headerText">Find an Opponent</h1>
                    <ToggleBar option1="Random" option2="Find Friend" value={this.state.mode}
                        changeValue={this.updateMode}/>
                    {this.state.mode === "Random" ? random : friend}
                </div>
                <InvalidPopup visible={this.state.invalidVisible} close={this.hideDenied}
                    message={this.state.invalidMessage}/>
                {this.state.socketError &&
                    <SocketErrorPopup click={this.toggleErrorPopup}/>
                }
            </div>
        );
    }
}

export default withRouter(FindOpponentScreen);