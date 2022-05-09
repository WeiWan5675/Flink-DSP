module.exports = {
    title: '基于Flink的数据流处理平台',
    description: '基于Flink的数据流处理平台',
    base: "/dsp/docs/",
    head: [
        ['link', { rel: 'icon', href: '/favicon.ico' }],
        ['meta', { name: 'keywords', content: 'vuepress,vuepress bar,vuepress sidebar,vuepress auto sidebar,vuepress 侧边栏,vuepress 自动生成侧边栏' }],
        ['meta', { name: 'viewport', content: 'width=device-width, initial-scale=1' }]
    ],
    locales: {
        '/en-US/': {
            lang: 'en-US',
            title: 'Flink-DSP',
            description: '基于Flink的数据流处理平台'
        },
        '/': {
            lang: 'zh-CN',
            title: 'Flink-DSP',
            description: '基于Flink的数据流处理平台'
        }
    },
    plugins: {
        "vuepress-plugin-auto-sidebar": {
            sort: {
                mode: "asc",
                readmeFirst: true,
                readmeFirstForce: false
            },
            title: {
                mode: "titlecase",
                map: {}
            },
            sidebarDepth: 2,
            collapse: {
                open: false,
                collapseList: [],
                uncollapseList: []
            },
            ignore: [],
            removeEmptyGroup: false,
            git: {
                trackStatus: 'all'
            }
        },
        "@vuepress/last-updated": {
            transformer: (timestamp, lang) => {
                const moment = require('moment');
                moment.locale(lang)
                return moment(timestamp).format('LLLL')
            }
        },
        "@vuepress/google-analytics": {
            ga: "UA-134613928-2"
        }
    },
    themeConfig: {
        lastUpdated: '上次更新',
        repo: 'WeiWan5675/Flink-DSP/tree/master/dsp-docs',
        editLinks: true,
        editLinkText: '编辑文档！',
        docsDir: 'docs',
        docsBranch: 'docs',
        locales: {
            '/': {
                label: '简体中文',
                selectText: '语言',
                ariaLabel: '选择语言',
                editLinkText: '在 GitHub 上编辑此页',
                lastUpdated: '上次更新',
                nav: [
                    { text: '首页', link: '/' },
                    { text: '用户文档', link: '/documents/about' },
                    { text: '常见问题', link: '/FAQ' },
                    { text: '更新日志🥳', link: '/ChangeLog' },
                ]
            },
            '/en-US/': {
                label: 'English',
                selectText: 'Languages',
                ariaLabel: 'Select language',
                editLinkText: 'Edit this page on GitHub',
                lastUpdated: 'Last Updated',
                nav: [
                    { text: 'Home', link: '/en-US/' },
                    { text: 'Documents', link: '/en-US/documents/about' },
                    { text: 'FAQ', link: '/en-US/FAQ' },
                    { text: 'CHANGELOG🥳', link: '/en-US/ChangeLog' },
                ]
            },
        }
    },
}