import React, {Component} from "react";
import logo from "../assets/logo.svg";
import PrimaryButton from "../components/PrimaryButton";
import TextBox from "../components/TextBox";
import InactiveButton from "../components/InactiveButton";
import UserContext from "../components/UserContext";
import {conn} from "../App";
import {withRouter} from 'react-router-dom';
import InvalidPopup from "../components/InvalidPopup";
import SocketErrorPopup from "../components/SocketErrorPopup";

/*
    Landing screen where users enter their nickname. Nicknames live as long as the web socket connection lasts
 */
class LandingScreen extends Component {

    static contextType = UserContext;

    constructor(props) {
        super(props);
        this.state = {
            invalid: false,
            socketError: false,
        }
        this.createUser = this.createUser.bind(this)
        this.hidePopup = this.hidePopup.bind(this)
        this.toggleErrorPopup = this.toggleErrorPopup.bind(this)
    }

    componentDidMount(){
        const { nickname, setVisible, setOpponentsList } = this.context;

        // Set up web socket connection listening
        conn.onmessage = (event) => {
            const msg = JSON.parse(event.data);

            console.log(msg);

            switch(msg.type) {
            // CONNECT message: listens for a new web socket connection
            case 0:
                console.log("connected");
                break;
            // USER message: Response from backend that determines if creating a new user was successful
                case 1:
                console.log("user")
                console.log(msg.payload.status)

                // go to next screen (/map) or show error message
                if (msg.payload.status === "success") {
                  this.props.history.push('/selectmap');
                } else {
                  this.setState({invalid: true})
                  console.log("error message: please select another name");
                }
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

    // Sends a request to the backend to create a new user with the inputted nickname
    createUser() {
        const { nickname } = this.context;
        
        const message = { 
            "type": 1,
            "payload": {
                "name" : nickname
            }
        }
        conn.send(JSON.stringify(message));
    }

    // Hides the invalid nickname error popup
    hidePopup() {
        this.setState({invalid: false})
    }

    // Toggles the visibility of a socket error popup
    toggleErrorPopup() {
        this.setState({socketError: !this.state.socketError})
    }

    render() {
        const { nickname, setNickname } = this.context

        return (
            <div className="landingContainer">
                <div className="middleContentContainer">
                    <img src={logo} className="landingLogo" alt="MapDash Logo"/>
                    <TextBox placeholder="Nickname" change={setNickname}/>
                    {nickname !== "" ?
                        <PrimaryButton label="Go!" onClick={this.createUser}/> :
                        <InactiveButton label="Go!"/>
                    }
                </div>
                <InvalidPopup visible={this.state.invalid} close={this.hidePopup}
                    message="This nickname is already taken! Please try another."/>
                {this.state.socketError &&
                    <SocketErrorPopup click={this.toggleErrorPopup}/>
                }
            </div>
        );
    }
}

export default withRouter(LandingScreen);