import Vue from 'vue'
import App from 'pages/App.vue'
import VueResource from 'vue-resource'
import '@babel/polyfill'
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css'

Vue.use(VueResource);
Vue.use(Vuetify);

new Vue({
    vuetify : new Vuetify({
        icons: {iconfont: 'mdiSvg'},
        defaultAssets: {
            font: true,
            icons: 'md'
        },
    }),
    el: '#app',
    render: a => a(App)
});