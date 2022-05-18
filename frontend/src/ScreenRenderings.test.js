import React from 'react';
import { render, screen } from '@testing-library/react';
import LandingScreen from './screens/LandingScreen';
import {BrowserRouter} from "react-router-dom";
import UserContext from "./components/UserContext";
import SelectMapScreen from "./screens/SelectMapScreen";
import FindOpponentScreen from "./screens/FindOpponentScreen";
import GameScreen from "./screens/GameScreen";

let globals

beforeAll(() => {
  const nickname = "Test User"
  const popupVisible = false
  const opponentsList = []
  const destination = []
  const start = []
  const opponent = ""
  const requestState = ""
  const map = ""
  const leader = ""
  const gameId = 0
  const location = ""
  const gameover = false

  const setNickname = () => {}
  const setVisible = () => {}
  const setOpponentsList = () => {}
  const setDestination = () => {}
  const setStart = () => {}
  const setOpponent = () => {}
  const setRequestState = () => {}
  const setMap = () => {}
  const setLeader = () => {}
  const setGameId = () => {}
  const setLocation = () => {}
  const setGameover = () => {}

  // Global variables for React Context so that different screens can access the same data
  globals = {
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
    setGameover: setGameover
  };
})

const renderWithRouterAndContext = (ui, route) => {
  window.history.pushState({}, 'Test page', route)
  return render(<UserContext.Provider value={globals}>ui</UserContext.Provider>, { wrapper: BrowserRouter })
}

test('LandingScreen renders without crashing', () => {
  renderWithRouterAndContext(<LandingScreen/>, '/')
});

test('SelectMapScreen renders without crashing', () => {
  renderWithRouterAndContext(<SelectMapScreen/>, '/map')
});

test('FindOpponentScreen renders without crashing', () => {
  renderWithRouterAndContext(<FindOpponentScreen/>, '/opponent')
});

test('GameScreen renders without crashing', () => {
  renderWithRouterAndContext(<GameScreen/>, '/play')
});
