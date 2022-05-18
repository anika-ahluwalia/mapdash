import './App.css';
import React, {useState, useEffect} from 'react';
import Router from "./components/Router";
import UserContext from './components/UserContext';

export const conn = new WebSocket("ws://localhost:4567/game");

/*
    The main App component that contains all of the global context variables, loads the GoogleMaps API script, and
    houses the Router component for the various screens
 */
function App () {

    const [nickname, setNickname] = useState("");
    const [map, setMap] = useState("");
    const [destination, setDestination] = useState([]);
    const [start, setStart] = useState([]);
    const [popupVisible, setVisible] = useState(false);
    const [opponent, setOpponent] = useState("");
    const [requestState, setRequestState] = useState("");
    const [opponentsList, setOpponentsList] = useState([]);
    const [leader, setLeader] = useState("");
    const [gameId, setGameId] = useState(0);
    const [location, setLocation] = useState("");
    const [gameover, setGameover] = useState(false);
    const [rematch, setRematch] = useState(false);
    const [inGame, setInGame] = useState(false);

    // Global variables for React Context so that different screens can access the same data
    const globals = {
      nickname: nickname,
      setNickname: setNickname,
      map: map,
      setMap: setMap,
      popupVisible: popupVisible,
      setVisible: setVisible,
      opponent: opponent,
      setOpponent: setOpponent,
      requestState: requestState,
      setRequestState: setRequestState,
      opponentsList: opponentsList,
      setOpponentsList: setOpponentsList,
      destination: destination,
      setDestination: setDestination,
      start: start,
      setStart: setStart,
      leader: leader,
      setLeader: setLeader,
      gameId: gameId,
      setGameId: setGameId,
      location: location,
      setLocation: setLocation,
      gameover: gameover,
      setGameover: setGameover,
      rematch: rematch,
      setRematch: setRematch,
      inGame: inGame,
      setInGame: setInGame
    };

    useEffect(() => {
        // Load the Google Maps Script if it hasn't already been loaded
        if (!window.google) {
            const googleMapsScript = document.createElement('script')
            googleMapsScript.src = `https://maps.googleapis.com/maps/api/js?key=${process.env.REACT_APP_MAPS_API_KEY}&libraries=places`
            googleMapsScript.async = true
            window.document.body.appendChild(googleMapsScript)
        }
    }, [])

    return (
        <UserContext.Provider value={globals}>
            <div className="App"> 
              <Router />
            </div>
        </UserContext.Provider>
    );
}

export default App;
