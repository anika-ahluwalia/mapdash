import React from 'react';
import {Switch, Route, BrowserRouter} from 'react-router-dom';
import LandingScreen from "../screens/LandingScreen";
import SelectMapScreen from "../screens/SelectMapScreen";
import GameScreen from "../screens/GameScreen";
import FindOpponentScreen from '../screens/FindOpponentScreen';
import RequestModal from './RequestModal';

/*
    Component for the react-router-dom routing functionality. Contains different Routes that
    render different screens depending on the current url
 */
class Router extends React.Component {

    constructor() {
        super();
        this.state = {
        }
    }
    
    render() {

        return (
            <BrowserRouter>
                <RequestModal />
                <Switch> {/* The Switch decides which component to show based on the current URL.*/}
                    <Route exact path='/' component={LandingScreen}></Route>
                    <Route exact path='/selectmap' component={SelectMapScreen}></Route>
                    <Route exact path='/opponent' component={FindOpponentScreen}></Route>
                    <Route exact path='/play' component={GameScreen}></Route>
                </Switch>
            </BrowserRouter>
        );
    }
}

export default Router;