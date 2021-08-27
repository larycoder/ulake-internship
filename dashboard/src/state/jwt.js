const initialState = {
    token: ""
};

const JWT_OBTAINED = 'jwt/obtained';

export const obtainJwt = token => ({
  type: JWT_OBTAINED, token
});

const jwtReducer = (state = initialState, action) => {
  switch (action.type) {
    case JWT_OBTAINED:
      return { ...state, token: action.token };
    default:
      return state;
  }
};

export default jwtReducer