import React from 'react';

import {Link} from 'react-router-dom';

export default function NoMatch() {
    return (
        <React.Fragment>
            <div className="container content-container">
                <h1>Not Found 404</h1>
                <div>There is nothing under this URL. Probably you want to go to <Link to="/">homepage</Link>.</div>
            </div>
        </React.Fragment>
    );
}
