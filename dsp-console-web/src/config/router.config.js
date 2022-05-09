// eslint-disable-next-line
import {BasicLayout, BlankLayout, PageView, RouteView, UserLayout} from '@/layouts'
import { bxAnaalyse } from '@/core/icons'

export const asyncRouterMap = [

  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '首页' },
    redirect: '/overview/dashboard',
    children: [
      // user
      {
        path: '/user',
        name: 'user',
        component: PageView,
        redirect: '/user/system-user',
        meta: { title: '用户管理', icon: 'check-circle-o', hiddenHeaderContent: true, hideHeader: true, permission: ['result'] },
        children: [
          {
            path: '/user/system-user',
            name: 'UserList',
            component: () => import(/* webpackChunkName: "result" */ '@/views/user/UserList'),
            meta: { title: '用户列表', keepAlive: false, hideHeader: true, hiddenHeaderContent: true, permission: ['result'] }
          }, {
            path: '/user/system-role',
            name: 'RoleList',
            component: () => import(/* webpackChunkName: "result" */ '@/views/user/RoleList'),
            meta: { title: '角色列表', keepAlive: false, hideHeader: true, hiddenHeaderContent: true, permission: ['result'] }
          }, {
            path: '/user/system-permission',
            name: 'PermissionList',
            component: () => import(/* webpackChunkName: "result" */ '@/views/user/PermissionList'),
            meta: { title: '权限列表', keepAlive: false, hideHeader: true, hiddenHeaderContent: true, permission: ['result'] }
          }
        ]
      },
      // dashboard
      {
        path: '/overview',
        name: 'overview',
        redirect: '/overview/dashboard',
        component: RouteView,
        meta: { title: 'Dashboard', keepAlive: true, hiddenHeaderContent: true, hideHeader: true, icon: bxAnaalyse, permission: ['dashboard'] },
        children: [
          {
            path: '/overview/running',
            name: 'Running',
            component: () => import('@/views/overview/Running'),
            meta: { title: 'Running State', keepAlive: false, hiddenHeaderContent: true, permission: ['running'] }
          }
        ]
      },
      // Exception
      {
        path: '/exception',
        name: 'exception',
        component: RouteView,
        redirect: '/exception/403',
        meta: { title: '异常页', icon: 'warning', hiddenHeaderContent: true, permission: ['exception'] },
        children: [
          {
            path: '/exception/403',
            name: 'Exception403',
            component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/403'),
            meta: { title: '403', permission: ['exception'] }
          },
          {
            path: '/exception/404',
            name: 'Exception404',
            component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404'),
            meta: { title: '404', permission: ['exception'] }
          },
          {
            path: '/exception/500',
            name: 'Exception500',
            component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/500'),
            meta: { title: '500', permission: ['exception'] }
          }
        ]
      }
    ]
  },
  {
    path: '*', redirect: '/404', hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Register')
      },
      {
        path: 'register-result',
        name: 'registerResult',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/RegisterResult')
      },
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },
  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }

]
