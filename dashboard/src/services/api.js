import React from 'react'
import { useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import Axios from "axios"
import { actions } from '../state/createStore'

export const backend = {
    endpoint: {
        user: "http://user.ulake.sontg.net",
        folder: "http://folder.ulake.sontg.net",
        core: "http://core.ulake.sontg.net",
    }
}

export const GetAPI = async (server, path) => {
    const jwt = useSelector((state) => state.jwt);
    const dispatch = useDispatch();
    const increase = useCallback(
        () => dispatch({type: actions.increment}),
        [dispatch]
    );
    const config = jwt ? {
        headers: { Authorization: `Bearer ${jwt}` }
    } : {};
    
    let resp = await Axios.get( 
      `${server}${path}`,
      config
    );
    console.log(resp);
}