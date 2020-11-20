import React from 'react';

import Header from './Header';
import Footer from "./Footer";
import Homepage from "./Homepage";
import NoMatch from "./NoMatch";

import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import JobDetails from "./JobDetails";

export default function App() {
    return (
        <React.Fragment>
            <Router>
                <Header/>
                <Switch>
                    <Route path="/job/:jobId">
                        <JobDetails/>
                    </Route>
                    <Route exact path="/">
                        <Homepage/>
                    </Route>
                    <Route path="*">
                        <NoMatch />
                    </Route>
                </Switch>
                <Footer/>
            </Router>
        </React.Fragment>
    );
}
