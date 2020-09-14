<template>
    <v-app>
        <v-app-bar height="100" app>
            <v-toolbar-title>visit service</v-toolbar-title>
        </v-app-bar>
        <v-main>
            <VisitTable></VisitTable>
        </v-main>
    </v-app>
</template>

<script>
    import VisitTable from "../components/VisitTable.vue";

    export default {
        name: "App",
        components: {VisitTable},
    }
</script>

<style scoped>

</style>