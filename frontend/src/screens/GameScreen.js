import React, {Component} from "react";
import GameToolbar from "../components/GameToolbar";
import HintButton from "../components/HintButton";
import DestinationMapButton from "../components/DestinationMapButton";
import DestinationPicButton from "../components/DestinationPicButton";
import DestinationPopup from "../components/DestinationPopup";
import WarningPopup from "../components/WarningPopup";
import HintIndicator from "../components/HintIndicator"
import Countdown from "react-countdown";
import StreetView from "../components/StreetView"
import UserContext from "../components/UserContext";
import ReturnToStartButton from "../components/ReturnToStartButton";
import {conn} from "../App";
import {withRouter} from 'react-router-dom';
import GameOverPopup from "../components/GameOverPopup";
import SocketErrorPopup from "../components/SocketErrorPopup";
import InvalidPopup from "../components/InvalidPopup";

/*
    Screen that displays the main game and StreetView component
 */
class GameScreen extends Component {

    timerRef = null
    static contextType = UserContext;

    constructor(props) {
        super(props);

        this.state = {
            leader: "Tied",
            hints : 3,
            destPopupOpen: false,
            destButtonClicked: "",
            displayWarning: false,
            displayHint: false,
            countdownOver: false,
            date: Date.now() + 5000,
            streetView: null,
            startStreetViewOptions: { addressControl: false, fullscreenControl: false, panControl: true},
            socketError: false,
            invalidVisible: false,
            invalidMessage: "",
            disconnect: false
        };

        this.setStreetView = this.setStreetView.bind(this)
        this.setHeading = this.setHeading.bind(this)
        this.toggleDestPopup = this.toggleDestPopup.bind(this)
        this.returnToStart = this.returnToStart.bind(this)
        this.displayWarning = this.displayWarning.bind(this)
        this.setTimerRef = this.setTimerRef.bind(this);
        this.start = this.start.bind(this)
        this.useHint = this.useHint.bind(this)
        this.onTimerUp = this.onTimerUp.bind(this);
        this.onPositionChanged = this.onPositionChanged.bind(this)
        this.toggleErrorPopup = this.toggleErrorPopup.bind(this)
        this.hideDenied = this.hideDenied.bind(this);
    }

    componentDidMount(){
        const { nickname, setVisible, setOpponentsList, setLeader} = this.context;

        this.context.setInGame(true);

        if (nickname === "") {
            this.props.history.push('/');
            return;
        }

        // Set up web socket connection listening
        conn.onmessage = (event) => {
            const msg = JSON.parse(event.data);

            console.log(msg);

            switch(msg.type) {
            case 2:
                this.hideDenied();
                this.context.setRequestState("received")
                this.context.setOpponent(msg.payload.player1)
                this.context.setLocation(msg.payload.location)
                this.context.setVisible(true)
                break;
            // ACCEPT message: accept a request
            case 3:
                this.hideDenied();
                // Set the destination name and location to be used for the game toolbar and reference picture
                this.context.setDestination(msg.payload.destination)
                this.context.setLocation(msg.payload.location)
                // Set the start panorama ID and location for StreetView
                this.context.setStart(msg.payload.start)
                this.context.setGameId(msg.payload.gameId);

                if (msg.payload.playerNumber === 1) {
                    this.context.setRequestState("accepted")
                    this.context.setVisible(true)
                }
                break;
            // DECLINE message: decline a request
            case 4:
                this.context.setRequestState("declined")
                this.context.setVisible(false)
                this.setState({invalidMessage: "Request Denied. Please try again"})
                this.setState({invalidVisible: true})
                break;
            // ACTIVE message: update active players list
            case 5:
                // getting updated every time a player joins / leaves
                console.log("received players");
                console.log(msg.payload.players);
                const players = msg.payload.players.replace(/['"]+/g, '').replace("[", '').replace("]", '');
                const listPlayers = players.split(",")
                console.log(listPlayers);
                setOpponentsList(listPlayers.filter(name => name !== nickname));
                break;
            // PLAY message: start game
            case 7:
                this.hideDenied();
                // this.context.setGameover(false);
                this.context.setRematch(true);
                this.context.setRequestState("")
                this.context.setGameover(false);
                this.props.history.push('/selectmap');
                this.props.history.push('/play');
                break;
            // MOVE message: after a move, determines which player is currently in the lead
            case 8:
                // update who's winning
                setLeader(msg.payload.leader);
                console.log(msg.payload)
                if(msg.payload.isGameOver){
                    this.onTimerUp()
                }
                break;
            // CANCEL message: cancel a request (only possible from player who sent it)
            case 9:
                console.log("received cancel");
                if (!this.context.gameover) {
                    this.setState({disconnect: true})
                    console.log("we should be here");
                    const message = { 
                        "type": 12,
                        "payload": {
                            "gameId" : -1,
                            "winner" : this.context.nickname
                        }
                    }
                    conn.send(JSON.stringify(message));
                } else {
                    this.hideDenied();
                    console.log("cancelling");
                    // cancelled request
                    this.context.setRequestState("")
                    this.context.setVisible(false)
                }
                break;
            // HINT message: changes the player's heading to point in the direction of the shortest path to
            // the destination and snaps them to the nearest node along that path
            case 10:
                this.setHeading(msg.payload)
                this.setState({displayHint: true})
                setTimeout(() => this.setState({displayHint: false}), 3000)
                console.log(msg)
                break;
            // GAMEOVER message: sets the winner and puts the game in the gameover state
            case 12:
                if (!this.context.rematch) {
                    console.log("game over received");
                    this.context.setLeader(msg.payload.winner);
                    this.context.setGameover(true);
                }
                break;
            // ERROR message: tells the frontend that the backend received some web socket error
            case 13:
                this.setState({socketError: true})
                break
            default:
                break;
            }
        }
        this.context.setInGame(true);
        this.context.setGameover(false);
        setVisible(false);
    }

    // Waits until the start location has been generated before initializing the StreetView
    componentDidUpdate(prevProps, prevState, snapshot) {
        // Only set up the initial StreetView options once we see that our start location has loaded in context
        if (this.state.streetView !== null && this.context.start.length > 0 && this.state.streetView.getPano() == null) {
            this.state.streetView.setPano(this.context.start[0])
        }
    }

    // Hides the denied request message popup
    hideDenied() {
        this.setState({invalidVisible: false})
    }

    // Setter method to allow GameScreen to access the StreetView
    setStreetView(streetView) {
        this.setState({streetView: streetView})
    }

    // Toggles the visibility of a socket error popup
    toggleErrorPopup() {
        this.setState({socketError: !this.state.socketError})
    }

    // Toggles what content should be displayed in the DestinationPopup component depending on which button was clicked
    toggleDestPopup(buttonClicked) {
        // Displays a map with the destination
        if (buttonClicked === "map") {
            this.setState({destButtonClicked: "map"})
        }
        // Displays a picture of the destination
        else if (buttonClicked === "pic") {
            this.setState({destButtonClicked: "pic"})
        }
        this.setState({destPopupOpen: !this.state.destPopupOpen})
    }

    // Displays a warning going in the wrong way popup after a certain number of incorrect moves
    displayWarning() {
        this.setState({displayWarning: true})
        setTimeout(() => this.setState({displayWarning: false}), 3000)
    }

    // Returns the StreetView back to its starting location
    returnToStart() {
        if (this.context.start.length > 0) {
            this.state.streetView.setPano(this.context.start[0])
        }
    }

    // Uses a hint to orient the player in the direction of the shortest path to the destination
    useHint() {
        if(this.state.hints > 0){
            this.setState(prev => { return {...prev, hints: prev.hints - 1}})
            console.log("GAME ID" + this.context.gameId)
            const message = { 
                "type": 10,
                "payload": {
                    "gameId": this.context.gameId,
                    "playerName" : this.context.nickname
                }
            }
            conn.send(JSON.stringify(message));
        }

    }

    // Changes the player's heading to face in the direction of the shortest path to their destination
    setHeading(payload) {
        const { heading, lat, lng } = payload
        this.state.streetView.setPosition(
            {
                lat,
                lng
            }
        )
        this.state.streetView.setPov(
            {
                heading,
                pitch: 0
            }
        )
    }

    // Starts the timer for the game
    start() {
        this.setState({countdownOver: true})
        this.timerRef.start()
    }

    // Sets the reference to the timer/countdown component
    setTimerRef(ref) {
        this.timerRef = ref;
    }

    // Renderer method for the countdown component
    renderer = ({seconds, completed }) => {
        // While countdown isn't over, display the seconds left
        if (!completed) {
            this.context.setRematch(true);
            this.context.setVisible(false);
            return <div className="countdownText">{seconds}</div>;
        } else {
            return null
        }
    }

    // Once timer is over, send game over message to backend
    onTimerUp() {
        console.log("times up!");

        this.context.setRematch(false);
        this.context.setInGame(false);
        this.context.setGameover(true);
        this.context.setRequestState("");
        if (this.context.leader === this.context.nickname || this.context.leader === "Tied") {
            const message = {
                "type": 12,
                "payload": {
                    "gameId" : this.context.gameId,
                }
            }
            conn.send(JSON.stringify(message));
        }
    }

    // When StreetView changes position, send a message to backend to update player's location
    onPositionChanged(position) {
        const { lat, lng } = position
        const message = {
            "type": 8,
            "payload": {
                "gameId": this.context.gameId,
                "playerName": this.context.nickname,
                "newLat": lat(),
                "newLong": lng()
            }
        }
        conn.send(JSON.stringify(message))
    }

    render() {
        return (
            <div className="gameContainer">
                <GameOverPopup hideDenied={this.hideDenied}
                    disconnect={this.state.disconnect}/>
                <GameToolbar
                    destination={this.context.destination}
                    leader={this.state.leader}
                    refCallback={this.setTimerRef}
                    hints = {this.state.hints}
                    onGameOver={this.onTimerUp}
                />
                {this.state.countdownOver
                    ?
                    <div style={{height: '100%'}}>
                        <StreetView
                            apiKey = {process.env.REACT_APP_MAPS_API_KEY}
                            streetViewPanoramaOptions={this.state.startStreetViewOptions}
                            setStreetView = {this.setStreetView}
                            onPositionChanged = {this.onPositionChanged}
                        />
                        {this.state.displayWarning &&
                            <WarningPopup/>
                        }
                        {this.state.displayHint &&
                            <HintIndicator/>
                        }
                        <div className="gameBottomLeftContainer">
                            {this.state.destPopupOpen
                                ? <DestinationPopup buttonClicked={this.state.destButtonClicked} click={this.toggleDestPopup}/>
                                :
                                <div className="gameButtonsContainer">
                                    <HintButton click={this.useHint}/>
                                    <ReturnToStartButton click={this.returnToStart}/>
                                    <DestinationMapButton click={this.toggleDestPopup}/>
                                    <DestinationPicButton click={this.toggleDestPopup}/>
                                </div>
                            }
                        </div>
                    </div>
                    :
                    <div className="countdownContainer">
                        <div className="streetViewContainer duringCountdown">
                            <div className="gameBottomLeftContainer">
                                {this.state.destPopupOpen
                                    ? <DestinationPopup buttonClicked={this.state.destButtonClicked} click={this.toggleDestPopup}/>
                                    :
                                    <div className="gameButtonsContainer">
                                        <HintButton click={this.useHint}/>
                                        <ReturnToStartButton click={this.returnToStart}/>
                                        <DestinationMapButton click={this.toggleDestPopup}/>
                                        <DestinationPicButton click={this.toggleDestPopup}/>
                                    </div>
                                }
                            </div>
                            <div className="countdownOverlay">
                                <Countdown renderer={this.renderer} date={this.state.date} onComplete={() => this.start()}/>
                            </div>
                        </div>
                    </div>
                }
                <InvalidPopup visible={this.state.invalidVisible} close={this.hideDenied}
                    message={this.state.invalidMessage}/>
                {this.state.socketError &&
                    <SocketErrorPopup inGame={true} click={this.toggleErrorPopup}/>
                }
            </div>
        );
    }
}

export default withRouter(GameScreen);