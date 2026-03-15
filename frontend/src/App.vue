<script setup>
import { ref } from 'vue'
import axios from 'axios'

const personalCode = ref('')
const amount = ref(4000)
const period = ref(12)

// response
const loanResult = ref(null)
const errorMessage = ref('')
const loading = ref(false)

const checkLoan = async () => {
  loading.value = true
  errorMessage.value = ''
  loanResult.value = null

  try {
    const response = await axios.get('/loan/check', { 
      params: {
        personalCode: personalCode.value,
        amount: amount.value,
        period: period.value
      }
    })
    loanResult.value = response.data
  } catch (error) { //handle error 400/404
    errorMessage.value = error.response?.data?.error || 'Error connecting to the server'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="app-wrapper">
    <div class="loan-card">
      <header class="card-header">
        <h1>Loan Calculator</h1>
      </header>

      <div class="form-body">
        <div class="input-section">
          <label class="input-label">Personal Code</label>
          <input 
            v-model="personalCode" 
            type="text" 
            placeholder="e.g. 49002010998"
            maxlength="11"
            class="text-input"
          />
          <span v-if="personalCode && personalCode.length < 11" class="hint">
            Enter 11 digits
          </span>
        </div>

        <div class="input-section">
          <div class="label-row">
            <label class="input-label">Amount</label>
            <span class="value-badge">{{ amount }} €</span>
          </div>
          <input type="range" v-model="amount" min="2000" max="10000" step="100" class="slider" />
          <div class="range-labels">
            <span>2000 €</span>
            <span>10 000 €</span>
          </div>
        </div>

        <div class="input-section">
          <div class="label-row">
            <label class="input-label">Period</label>
            <span class="value-badge">{{ period }} months</span>
          </div>
          <input type="range" v-model="period" min="12" max="60" class="slider" />
          <div class="range-labels">
            <span>12 m</span>
            <span>60 m</span>
          </div>
        </div>

        <button 
          @click="checkLoan" 
          :disabled="loading || personalCode.length < 11" 
          class="submit-btn"
        >
          <span v-if="!loading">Check Offer</span>
          <span v-else class="loader">Processing...</span>
        </button>

        <div v-if="errorMessage" class="error-toast">
          {{ errorMessage }}
        </div>
      </div>

      <transition name="fade">
        <div v-if="loanResult" :class="['result-overlay', loanResult.status]">
          <div class="result-content">
            <div class="icon">
              <span v-if="loanResult.status === 'APPROVED'">✔</span>
              <span v-else>✖</span>
            </div>
            <h3>{{ loanResult.status }}</h3>
            
            <p v-if="loanResult.status === 'APPROVED'">
              Maximum sum: <strong>{{ loanResult.approvedAmount }} €</strong><br>
              Period: <strong>{{ loanResult.period }} months</strong>
            </p>
            <p v-else-if="loanResult.status === 'DEBT'">
              Application rejected due to existing debt record.
            </p>
            <p v-else>
              No offer available for the selected parameters.
            </p>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>


