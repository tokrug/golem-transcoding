import React from 'react';
import axios from 'axios';
import { useHistory } from "react-router-dom";

import { useForm } from "react-hook-form";

export default function Homepage() {

    const history = useHistory();

    const { register, handleSubmit, watch, errors, formState } = useForm({ mode: 'onChange' });
    const watchFile = watch("file");

    const onSubmit = data => {
        const requestData = new FormData();
        requestData.append("files", data.file[0]);
        axios.post('/api/transcode', requestData)
            .then(response => {
                history.push("/job/" + response.data.id);
            });
    };

    const currentFilename = () => {
        return watchFile && watchFile[0]
            ? watchFile[0].name
            : "Choose file";
    }

    return (
        <React.Fragment>
            <div className="container content-container">
                <div className="jumbotron bg-white">
                    <h1 className="display-4">Golem Transcoding</h1>
                    <p className="lead">
                        Welcome to Golem Transcoding on Yagna testnet!
                    </p>
                    <p>
                        You can upload a video file using the form below.
                        It will be sent to the backend server where Golem requestor script will be invoked to transcode the input file into multiple containers.
                        Currently there are four different containers supported: .avi, .mkv, .mp4 and .mov.
                    </p>
                    <p>
                        Once you upload a file you will be redirected to the job processing details page.
                        Job status is updated through Server Sent Events so there's no need to refresh the page.
                        Once transcoding is done you will be presented with links to output files.
                        If processing takes a long time be sure to store the job ID! This is the only way to access output files.
                    </p>
                    <p>
                        Due to server constraints where this application is deployed uploaded file size must be smaller than 10 MB.
                        Output files are removed automatically an hour after the transcoding job has been finished.
                    </p>
                    <hr className="my-4" />
                    <div className="card">
                        <div className="card-header">
                            Upload form
                        </div>
                        <div className="card-body">
                            <form onSubmit={handleSubmit(onSubmit)}>
                                <div className="form-group row">
                                    <label className="col-sm-2 col-form-label" htmlFor="fileUpload">Video input</label>
                                    <div className="col-sm-10">
                                        <div className="custom-file">
                                            <input type="file" accept="video/*" name="file" className="custom-file-input" id="fileUpload" aria-describedby="fileUploadHelp" ref={register({
                                                required: true,
                                                validate: {
                                                    fileSize: value => value[0].size < 10 * 1024 * 1024
                                                } })} />
                                            <label className="custom-file-label" htmlFor="fileUpload">{currentFilename()}</label>
                                            <small id="fileUploadHelp" className="form-text text-muted">Video to be encoded to different formats.</small>
                                            {errors.file && errors.file.type === 'fileSize' && (
                                                <small className="form-text text-danger">File must be smaller than 10 MB!</small>
                                            )}
                                        </div>
                                    </div>
                                </div>
                                {
                                    (!formState.isDirty || errors.file) && (
                                        <button type="submit" className="btn btn-primary" disabled>Submit</button>
                                    )
                                }
                                {
                                    (formState.isDirty && !errors.file) && (
                                        <button type="submit" className="btn btn-primary">Submit</button>
                                    )
                                }
                            </form>
                        </div>
                    </div>
                </div>


            </div>
        </React.Fragment>
    );
}
