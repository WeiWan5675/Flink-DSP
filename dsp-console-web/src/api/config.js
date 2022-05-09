import { axios } from '@/utils/request'

const api = {
  configList: '/config/list',
  configSave: '/config/create',
  configUpdate: '/config/update',
  configDelete: '/config/delete'
}

export default api

export function configList (parameter) {
  return axios({
    url: api.configList,
    method: 'post',
    data: parameter
  })
}
export function configSave (parameter) {
  return axios({
    url: api.configSave,
    method: 'post',
    data: parameter
  })
}
export function configUpdate (parameter) {
  return axios({
    url: api.configUpdate,
    method: 'post',
    data: parameter
  })
}

export function configDelete (parameter) {
  return axios({
    url: api.configDelete,
    method: 'delete',
    data: parameter
  })
}
