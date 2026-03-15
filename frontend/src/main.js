import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import axios from 'axios'


axios.defaults.baseURL = 'http://localhost:8080/api'

// log errors in console
axios.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error.response?.data || error.message)
    return Promise.reject(error)
  }
)


createApp(App).mount('#app')


