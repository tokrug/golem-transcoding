import React, { useEffect, useState } from 'react';
import {
    useParams
} from "react-router-dom";

import NoMatch from "./NoMatch";

import axios from 'axios';

export default function JobDetails() {

    let { jobId } = useParams();

    let [details, setDetails] = useState(null);
    let [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get('/api/transcode/' + jobId)
            .then(response => {
                setDetails(response.data);
                setLoading(false);
            })
            .catch(error => {
                setLoading(false);
            });
    }, [jobId]);

    useEffect(() => {
        if (!loading) {
            const evtSource = new EventSource("/api/transcode/" + jobId);
            evtSource.onmessage = event => {
                setDetails(JSON.parse(event.data));
                if (event.data.completed) {
                    evtSource.close();
                }
            };
            return () => evtSource.close();
        }
    }, [loading]);

    const calculateOutputPath = (jobId, ext) => {
        return `/output/${jobId}/output.${ext}`;
    }

    const renderOutputs = () => {
        return (
            <div>
                {
                    (details && details.completedFormats && details.completedFormats.length > 0) && (
                        <div>
                            <div>Download:</div>
                            <ul>
                                {
                                    details.completedFormats.map(ext => {
                                        return (
                                            <li key={ext}>
                                                <a href={calculateOutputPath(details.id, ext)} download>.{ext}</a>
                                            </li>
                                        );
                                    })
                                }
                            </ul>
                        </div>
                    )
                }
            </div>
        );
    };

    const displayProgressSpinner = (state) => {
        if (state === "in progress") {
            return (
                <div className="spinner-border spinner-border-sm text-info" role="status">
                    <span className="sr-only">Loading...</span>
                </div>
            )
        }
    }

    const renderState = () => {
        let state;
        if (details && details.completed) {
            state = details.error ? "error" : "finished";
        } else {
            state = "in progress"
        }
        return (
            <div>
                <div>Status: {state} {displayProgressSpinner(state)}</div>
            </div>
        );
    }

    return (
        <React.Fragment>
            {
                loading && (
                    <div className="container content-container d-flex justify-content-center align-items-center">
                        <div className="spinner-border spinner-border-lg text-info" role="status">
                            <span className="sr-only">Loading...</span>
                        </div>
                    </div>
                )
            }
            {
                (!loading && details) && (
                    <div className="container content-container">
                        <div className="jumbotron bg-white">
                            <h1 className="display-4">Transcoding job details</h1>
                            <p>

                            </p>
                            <hr className="my-4" />
                            <div className="card">
                                <div className="card-header">
                                    {details.filename}
                                </div>
                                <div className="card-body">
                                    ID: {jobId}
                                    {renderState()}
                                    {details && renderOutputs()}
                                </div>
                            </div>
                        </div>
                    </div>
                )
            }
            {
                (!loading && !details) && (
                    <NoMatch />
                )
            }
        </React.Fragment>
    );
}
