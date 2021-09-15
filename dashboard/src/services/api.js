import React from 'react'
import { useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import Axios from "axios"
import { actions } from '../state/createStore'

export const backend = {
    endpointz: {
        user: "http://user.ulake.sontg.net",
        folder: "http://folder.ulake.sontg.net",
        core: "http://core.ulake.sontg.net",
    },
    endpoint: {
        core: "http://loclahost:8784",
        user: "http://localhost:8785",
        folder: "http://localhost:8786",        
    }
}

export const GetAPI = async (server, path, jwt) => {
    // const jwt = useSelector((state) => state.jwt);
    // const dispatch = useDispatch();
    // const increase = useCallback(
    //     () => dispatch({type: actions.increment}),
    //     [dispatch]
    // );
    console.log(`Making request to ${backend.endpoint[server]}${path}`);
    const config = jwt ? {
        headers: { Authorization: `Bearer ${jwt}` }
    } : {};
    
    let resp = await Axios.get( 
      `${backend.endpoint[server]}${path}`,
      config
    );
    console.log(resp);
}

/*

host="http://user.ulake.sontg.net";
host="http://127.0.0.1:8785";
fetch(`${host}/api/auth/login`, {
	method: 'POST',
	headers: {
		'Content-Type': 'application/json'			
	},
	body: JSON.stringify({
		userName: 'usr',
		password: 'pass'
	})
})
.then(resp => resp.json())
.then(json => json.resp)
.then(token => {
	fetch(`${host}/api/user/search`, { 
			method: 'POST',
			body: JSON.stringify({
				keywords: ['usr']
			}),
			headers: {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${token}`
			}
	})
	.then(resp => resp.json())
	.then(json => console.log(json))
})

*/