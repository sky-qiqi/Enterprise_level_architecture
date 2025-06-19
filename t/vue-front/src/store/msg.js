// msg.js
import { marked } from 'marked'
import axios from 'axios'

export default {
    namespaced: true,
    state: {
        list: []
    },
    mutations: {
        ADD_USER_MSG(state, { text, image }) {
            state.list.push({
                role: 'user',
                content: text,
                image: image || null,
                status: 2
            })
        },
        ADD_AI_MSG(state, { content, status, index = 0 }) {
            let runMsg = state.list.find(i => i.status !== 2)
            if (!runMsg) {
                state.list.push({
                    role: 'assistant',
                    content: content || '处理中...', // Initial loading state
                    status: status,
                    index: index // Track typing progress
                })
            } else {
                runMsg.content = content || '处理中...' // Replace content
                runMsg.status = status
                runMsg.index = index // Update typing index
                if (status === 2) {
                    runMsg.content = marked(runMsg.content || '暂无内容') // Mark when final
                }
            }
        },
        UPDATE_AI_CONTENT(state, { index, content }) {
            let runMsg = state.list.find(i => i.status !== 2)
            if (runMsg) {
                runMsg.content = content
                runMsg.index = index
            }
        },
        CLEAR_MESSAGES(state) {
            state.list = []
        }
    },
    actions: {
        userAddMsg({ commit }, { text, image }) {
            commit('ADD_USER_MSG', { text, image })
        },
        async aiAddMsg({ commit, dispatch }, { text, image }) {
            commit('ADD_AI_MSG', { content: '处理中...', status: 1 }) // Start with loading state
            try {
                const response = await axios.post('http://localhost:8080/api/chat/send', {
                    model: 'doubao-1-5-thinking-vision-pro-250428',
                    messages: [
                        {
                            role: 'user',
                            content: [
                                { type: 'text', text: text || '请描述图片内容' },
                                ...(image ? [{ type: 'image_url', image_url: image }] : [])
                            ]
                        }
                    ]
                }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })

                const aiContent = response.data.content || 'API 返回为空'
                // Simulate typing effect
                for (let i = 0; i <= aiContent.length; i++) {
                    commit('UPDATE_AI_CONTENT', { index: i, content: aiContent.slice(0, i) })
                    await new Promise(resolve => setTimeout(resolve, 100)) // 100ms per character
                }
                commit('ADD_AI_MSG', { content: aiContent, status: 2 }) // Finalize with marked content
            } catch (error) {
                commit('ADD_AI_MSG', {
                    content: `错误: ${error.response?.data?.message || '无法连接到后端'}`,
                    status: 2
                })
                console.error('Backend error:', error)
            }
        }
    }
}