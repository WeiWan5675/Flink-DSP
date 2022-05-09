import { axios } from '@/utils/request'

const api = {
  engineMode: '/dict/engineMode',
  engineType: '/dict/engineType',
  applicationType: '/dict/applicationType',
  getApplicationStates: '/dict/applicationState',
  getUnresolvedHandlerType: '/dict/unresolvedHandlerType',
  engineConfig: '/template/engineConfigs',
  getCollectorHandlerConfig: '/template/collectorHandlerConfigs',
  list: '/application/list',
  saveApplication: '/application/create',
  updateApplication: '/application/update',
  getApplicationOverview: 'metrics/application/overview',
  queryApp: '/application/query',
  deleteApplication: 'application/delete',
  startApplication: 'application/start',
  stopApplication: 'application/stop'
}

export default api

export function saveApplication (parameter) {
  return axios({
    url: api.saveApplication,
    method: 'post',
    data: parameter
  })
}
export function startApplication (parameter) {
  return axios({
    url: api.startApplication,
    method: 'post',
    data: parameter
  })
}
export function stopApplication (parameter) {
  return axios({
    url: api.stopApplication,
    method: 'post',
    data: parameter
  })
}
export function deleteApplication (parameter) {
  return axios({
    url: api.deleteApplication,
    method: 'delete',
    params: parameter
  })
}
export function updateApplication (parameter) {
  return axios({
    url: api.updateApplication,
    method: 'post',
    data: parameter
  })
}
export function queryApp (parameter) {
  return axios({
    url: api.queryApp,
    method: 'post',
    data: parameter
  })
}
export function engineMode (parameter) {
  return axios({
    url: api.engineMode,
    method: 'get',
    params: parameter
  })
}
export function getApplicationOverview () {
  return axios({
    url: api.getApplicationOverview,
    method: 'get'
  })
}
export function engineType () {
  return axios({
    url: api.engineType,
    method: 'get'
  })
}
export function getApplicationStates () {
  return axios({
    url: api.getApplicationStates,
    method: 'get'
  })
}
export function applicationType () {
  return axios({
    url: api.applicationType,
    method: 'get'
  })
}
export function getUnresolvedHandlerType () {
  return axios({
    url: api.getUnresolvedHandlerType,
    method: 'get'
  })
}
export function engineConfig (parameter) {
  return axios({
    url: api.engineConfig,
    method: 'get',
    params: parameter
  })
}
export function getCollectorHandlerConfig (parameter) {
  return axios({
    url: api.getCollectorHandlerConfig,
    method: 'get',
    params: parameter
  })
}
export function list (parameter) {
  return axios({
    url: api.list,
    method: 'post',
    data: parameter
  })
}
