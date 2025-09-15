import axios from 'axios';

// API url , all call will be from here
const API_URL = 'http://localhost:8080/api/auth';


const login = async (email, password) => {
  // Make a post request /api/auth/login
  const response = await axios.post(API_URL + '/login', {
    email,
    password,
  });
  // Save the datas with the token jwt
  return response.data;
};


export default {
  login,
};