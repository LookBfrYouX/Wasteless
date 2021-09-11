<template>
  <v-expansion-panels>
    <v-expansion-panel>
      <v-expansion-panel-header>
        Nutrition Facts
      </v-expansion-panel-header>
      <v-expansion-panel-content>
        <v-row>
          <v-col cols="12" md="6">
            <div class="d-flex justify-space-around">
              <v-img
                  contain
                  max-width="30%"
                  src="https://picsum.photos/id/11/500/300">
              </v-img>
              <v-img
                  contain
                  max-width="50%"
                  src="https://picsum.photos/id/11/500/300">
              </v-img>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            Second column
          </v-col>
        </v-row>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
export default {
  name: "NutritionFacts"
}
</script>

<style scoped>

</style>