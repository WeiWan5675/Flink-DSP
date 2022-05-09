import { axios } from '@/utils/request'

const api = {
  scheduleList: '/schedule/list',
  scheduleSave: '/schedule/create',
  scheduleUpdate: '/schedule/update',
  scheduleDelete: '/schedule/delete',
  scheduleStart: '/schedule/load',
  scheduleStop: '/schedule/stop',
  scheduleResume: '/schedule/resume',
  scheduleRestart: '/schedule/reload'
}

export default api

export function scheduleList (parameter) {
  return axios({
    url: api.scheduleList,
    method: 'post',
    data: parameter
  })
}
export function scheduleSave (parameter) {
  return axios({
    url: api.scheduleSave,
    method: 'post',
    data: parameter
  })
}
export function scheduleUpdate (parameter) {
  return axios({
    url: api.scheduleUpdate,
    method: 'post',
    data: parameter
  })
}
export function scheduleDelete (parameter) {
  return axios({
    url: api.scheduleDelete,
    method: 'post',
    data: parameter
  })
}
export function scheduleStart (parameter) {
  return axios({
    url: api.scheduleStart,
    method: 'post',
    data: parameter
  })
}
export function scheduleStop (parameter) {
  return axios({
    url: api.scheduleStop,
    method: 'post',
    data: parameter
  })
}
export function scheduleResume (parameter) {
  return axios({
    url: api.scheduleResume,
    method: 'post',
    data: parameter
  })
}
export function scheduleRestart (parameter) {
  return axios({
    url: api.scheduleRestart,
    method: 'post',
    data: parameter
  })
}
