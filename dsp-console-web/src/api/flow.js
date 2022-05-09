import { axios } from '@/utils/request'

const api = {
  flowList: '/flow/list',
  flowSave: '/flow/create',
  flowUpdate: '/flow/update',
  disableFlow: '/flow/disable',
  deleteFlow: '/flow/delete',
  flowDownload: '/flow/download',
  checkedFlow: '/flow/checked',
  flowUpload: '/flow/upload'
}

export default api

export function flowSave (parameter) {
  return axios({
    url: api.flowSave,
    method: 'post',
    data: parameter
  })
}
export function flowUpload (parameter) {
  return axios({
    url: api.flowUpload,
    method: 'post',
    data: parameter
  })
}
export function flowUpdate (parameter) {
  return axios({
    url: api.flowUpdate,
    method: 'post',
    data: parameter
  })
}
export function flowList (parameter) {
  return axios({
    url: api.flowList,
    method: 'post',
    data: parameter
  })
}

export function disableFlow (parameter) {
  return axios({
    url: api.disableFlow,
    method: 'put',
    data: parameter
  })
}

export function checkedFlow (parameter) {
  return axios({
    url: api.checkedFlow,
    method: 'put',
    data: parameter
  })
}

export function deleteFlow (parameter) {
  return axios({
    url: api.deleteFlow,
    method: 'delete',
    data: parameter
  })
}

export function flowDownload (parameter) {
  return axios({
    url: api.flowDownload,
    method: 'get',
    params: parameter,
    responseType: 'blob'
  })
}
