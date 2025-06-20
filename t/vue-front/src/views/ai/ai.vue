<!-- ai.vue -->
<template>
  <div>
    <Header />
    <div class="ai-chat">
      <div class="chat-container">
        <div class="action-bar">
          <button class="clear-button" @click="clearChat">
            <el-icon><Delete /></el-icon>
            清除对话内容
          </button>
        </div>
        <div class="message-container" id="message-box">
          <div
              v-for="(msg, index) in messageList"
              :key="index"
              :class="['message', msg.role === 'user' ? 'message-user' : 'message-assistant']"
          >
            <div class="message-content">
              <div class="avatar">
                <img
                    :src="msg.role === 'user' ? userImg : aiImg"
                    :alt="msg.role === 'user' ? '用户' : 'AI'"
                    class="avatar-img"
                />
                <span v-if="msg.role === 'assistant'" class="assistant-name">豆包AI</span>
              </div>
              <div class="bubble">
                <img
                    v-if="msg.role === 'user' && msg.image"
                    :src="msg.image"
                    alt="Uploaded image"
                    class="uploaded-image"
                />
                <div class="text" v-html="msg.content || '暂无内容'"></div>
              </div>
            </div>
          </div>
        </div>
        <div class="input-area">
          <textarea
              class="message-input"
              rows="3"
              placeholder="请输入您的问题..."
              v-model="msgValue"
              @keydown.enter.prevent="submitMsg"
          ></textarea>
          <input
              type="file"
              accept="image/*"
              @change="handleImageUpload"
              class="image-upload"
              ref="imageInput"
          />
          <button class="send-button" @click="submitMsg">
            发送
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import aiImg from '../../assets/imgs/豆包.webp'
import Header from '@/components/header.vue'
import userImg from '../../assets/imgs/用户头像.png'

export default {
  name: 'AiChat',
  components: {
    Header
  },
  data() {
    return {
      msgValue: '',
      msgDom: null,
      userImg: userImg,
      aiImg: aiImg,
      uploadedImage: null
    }
  },
  computed: {
    messageList() {
      return this.$store.state.msg.list || []
    }
  },
  mounted() {
    this.msgDom = document.getElementById('message-box')
    this.scroll()

    window.copyCode = (button) => {
      const codeBlock = button.closest('.code-block').querySelector('code')
      const text = codeBlock.textContent

      navigator.clipboard.writeText(text).then(() => {
        this.$message({
          message: '代码已复制到剪贴板',
          type: 'success',
          duration: 2000
        })

        button.innerHTML = '<i class="el-icon-check"></i>'
        setTimeout(() => {
          button.innerHTML = '<i class="el-icon-document-copy"></i>'
        }, 2000)
      }).catch(err => {
        this.$message.error('复制失败，请手动复制')
        console.error('复制失败:', err)
      })
    }
  },
  methods: {
    scroll() {
      this.$nextTick(() => {
        if (this.msgDom) {
          this.msgDom.scrollTop = this.msgDom.scrollHeight
        }
      })
    },
    handleImageUpload(event) {
      const file = event.target.files[0]
      if (file) {
        if (!file.type.startsWith('image/')) {
          this.$message.error('请上传图片文件')
          return
        }
        const reader = new FileReader()
        reader.onload = e => {
          this.uploadedImage = e.target.result
          this.$message.success('图片已选择')
        }
        reader.readAsDataURL(file)
      }
    },
    async submitMsg() {
      if (!this.msgValue.trim() && !this.uploadedImage) {
        this.$message.warning('请输入文本或选择图片')
        return
      }

      this.$store.dispatch('msg/userAddMsg', {
        text: this.msgValue || '请描述图片内容',
        image: this.uploadedImage
      })
      await this.$store.dispatch('msg/aiAddMsg', {
        text: this.msgValue || '请描述图片内容',
        image: this.uploadedImage
      })

      this.msgValue = ''
      this.uploadedImage = null
      this.$refs.imageInput.value = ''
      this.scroll()
    },
    clearChat() {
      this.$confirm('确认清除所有对话记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.commit('msg/CLEAR_MESSAGES')
        this.$message({
          type: 'success',
          message: '对话已清除'
        })
        this.scroll()
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消清除'
        })
      })
    },
    processCodeBlocks(content) {
      if (!content) return content

      content = content.replace(/<pre><code.*?>([\s\S]*?)<\/code><\/pre>/g, (match, code) => {
        return `
          <div class="code-block">
            <div class="code-header">
              <button class="copy-button" onclick="copyCode(this)">
                <i class="el-icon-document-copy"></i>
              </button>
            </div>
            <pre><code>${code}</code></pre>
          </div>
        `
      })

      content = content.replace(/<pre><code class="language-.*?">([\s\S]*?)<\/code><\/pre>/g, (match, code) => {
        return `
          <div class="code-block">
            <div class="code-header">
              <button class="copy-button" onclick="copyCode(this)">
                <i class="el-icon-document-copy"></i>
              </button>
            </div>
            ${match}
          </div>
        `
      })

      return content
    }
  },
  watch: {
    messageList: {
      handler() {
        this.scroll()
      },
      deep: true
    }
  }
}
</script>

<style scoped lang="less">
.ai-chat {
  height: 83vh;
  padding: 20px 0;
  min-height: calc(100vh - 380px);
  background-color: #f5f5f5;

  .chat-container {
    padding-top: 20px;
    max-width: 1000px;
    margin: 0 auto;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    height: calc(100vh - 260px);
    background: url('../../assets/imgs/bg-1.jpg') no-repeat;
  }

  .message-container {
    flex: 1;
    overflow-y: auto;
    padding: 24px;
    scroll-behavior: smooth;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: #ddd;
      border-radius: 3px;
    }
  }

  .message {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .message-content {
    display: flex;
    gap: 12px;
    align-items: flex-start;
  }

  .message-user {
    .message-content {
      flex-direction: row-reverse;
    }

    .bubble {
      background-color: #1a60ea;
      color: white;
      border-radius: 12px 12px 0 12px;
    }
  }

  .message-assistant {
    .bubble {
      background-color: #f7f7f7;
      border-radius: 12px 12px 12px 0;
    }
  }

  .avatar {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;

    .avatar-img {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .assistant-name {
      font-size: 12px;
      color: #666;
    }
  }

  .bubble {
    padding: 12px 16px;
    max-width: 80%;

    .text {
      line-height: 1.6;
      font-size: 14px;

      :deep(pre) {
        background-color: #2d2d2d;
        color: #fff;
        padding: 16px;
        border-radius: 8px;
        margin: 8px 0;
        overflow-x: auto;

        code {
          font-family: 'Fira Code', monospace;
          font-size: 13px;
        }
      }

      :deep(p) {
        margin: 8px 0;
      }
    }

    .uploaded-image {
      max-width: 200px;
      max-height: 200px;
      border-radius: 8px;
      margin-bottom: 8px;
    }
  }

  .input-area {
    padding: 20px;
    border-top: 1px solid #eee;
    display: flex;
    gap: 16px;
    align-items: flex-end;
    background: #fff;
    border-radius: 0 0 12px 12px;

    .message-input {
      flex: 1;
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 12px;
      resize: none;
      font-size: 14px;
      line-height: 1.5;
      min-height: 70px;
      transition: border-color 0.3s;

      &:focus {
        outline: none;
        border-color: #1a60ea;
      }

      &::placeholder {
        color: #999;
      }
    }

    .image-upload {
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 8px;
      cursor: pointer;
    }

    .send-button {
      padding: 12px 28px;
      background-color: #1a60ea;
      color: white;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      font-size: 14px;
      transition: background-color 0.3s;
      height: 44px;

      &:hover {
        background-color: #1550c8;
      }
    }
  }

  .action-bar {
    padding: 12px 20px;
    display: flex;
    justify-content: flex-end;

    .clear-button {
      padding: 8px 16px;
      background-color: #fff;
      border-radius: 6px;
      color: #666;
      cursor: pointer;
      font-size: 14px;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: all 0.3s;

      &:hover {
        background-color: #f5f5f5;
        color: #ff4d4f;
        border-color: #ff4d4f;
      }

      i {
        font-size: 16px;
      }
    }
  }

  :deep(.code-block) {
    position: relative;
    margin: 1em 0;

    .code-header {
      position: absolute;
      right: 10px;
      top: 10px;
      z-index: 2;
    }

    .copy-button {
      background: rgba(255, 255, 255, 0.1);
      border: none;
      border-radius: 4px;
      color: #fff;
      cursor: pointer;
      padding: 4px 8px;
      font-size: 14px;
      transition: all 0.3s;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }

      i {
        font-size: 16px;
      }
    }

    pre {
      margin: 0;
      position: relative;
      padding-top: 2.5em !important;
    }
  }

  :deep(.el-icon-check) {
    color: #67C23A;
  }
}

@media screen and (max-width: 768px) {
  .ai-chat {
    padding: 0;

    .chat-container {
      max-width: 100%;
      border-radius: 0;
      height: calc(100vh - 380px);
    }

    .message-container {
      padding: 16px;
    }

    .bubble {
      max-width: 90%;
    }

    .input-area {
      padding: 16px;
    }
  }
}
</style>
