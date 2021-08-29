import Axios from "axios"

export const backend = {
    endpoint: {
        user: "http://user.ulake.sontg.net",
        folder: "http://folder.ulake.sontg.net",
        core: "http://core.ulake.sontg.net",
    }
}

export const get = async (server, path) => {
    const config = {
        headers: { Authorization: `Bearer token` }
    };
    
    let resp = await Axios.get( 
      `${server}${path}`,
      config
    );
    console.log(resp);
}