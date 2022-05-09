import { axios } from '@/utils/request'

const api = {
  pluginList: '/plugin/list',
  pluginCreate: '/plugin/create',
  pluginUpdate: '/plugin/update',
  pluginDisable: '/plugin/disable',
  pluginDelete: '/plugin/delete',
  pluginUpload: '/plugin/upload',
  pluginTypes: '/dict/pluginType',
  pluginVerify: 'plugin/verify',
  pluginUpdatePart: 'plugin/updatePart',
  plugins: '/plugin/plugins'
}

export default api

export function pluginList (parameter) {
  return axios({
    url: api.pluginList,
    method: 'post',
    data: parameter
  })
}
export function plugins (parameter) {
  return axios({
    url: api.plugins,
    method: 'get',
    data: parameter
  })
}
export function pluginTypes (parameter) {
  return axios({
    url: api.engineMode,
    method: 'get',
    params: parameter
  })
}
export function pluginDisable (parameter) {
  return axios({
    url: api.pluginDisable,
    method: 'put',
    data: parameter
  })
}
export function pluginDelete (parameter) {
  return axios({
    url: api.pluginDelete,
    method: 'delete',
    data: parameter
  })
}
export function pluginVerify (parameter) {
  return axios({
    url: api.pluginVerify,
    method: 'post',
    data: parameter
  })
}

export function pluginCreate (parameter) {
  return axios({
    url: api.pluginCreate,
    method: 'post',
    data: parameter
  })
}
export function pluginUpdate (parameter) {
  return axios({
    url: api.pluginUpdate,
    method: 'put',
    data: parameter
  })
}
export function pluginUpload (parameter) {
  // 手动设置上传请求参数,将content-type改为multipart/form-data,同时指定超时时间
  const headersConfig = {
    timeout: 60,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }
  const res = axios({
    url: api.pluginUpload,
    method: 'post',
    data: parameter,
    headersConfig
  })
  return res
}
export function pluginUpdatePart (parameter) {
  return axios({
    url: api.pluginUpdatePart,
    method: 'put',
    data: parameter
  })
}
