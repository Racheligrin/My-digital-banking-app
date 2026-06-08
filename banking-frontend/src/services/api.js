import axios from 'axios';

// כתובת השרת של ה-Spring Boot (רכיב ה-Backend שלכן)
const API_BASE_URL = "http://localhost:8080/api/accounts";

export const accountService = {
    // 1. יצירת חשבון חדש (ומשתמש)
    createAccount: async (accountData) => {
        const response = await axios.post(API_BASE_URL, accountData);
        return response.data;
    },

    // 2. שליפת פרטי חשבון + היסטוריית פעולות לפי מספר חשבון
    getAccount: async (accountNumber) => {
        const response = await axios.get(`${API_BASE_URL}/${accountNumber}`);
        return response.data;
    },

    // 3. פונקציה מעניינת 1: העברת כספים בין חשבונות
    transferMoney: async (source, dest, amount) => {
        const response = await axios.post(`${API_BASE_URL}/transfer`, null, {
            params: { sourceAccountNumber: source, destAccountNumber: dest, amount: amount }
        });
        return response.data;
    },

    // 4. פונקציה מעניינת 2: הפקדה או משיכה
    updateBalance: async (accountNumber, operationType, amount) => {
        const response = await axios.put(`${API_BASE_URL}/${accountNumber}/balance`, null, {
            params: { operationType, amount }
        });
        return response.data;
    },

    // 5. פונקציה מעניינת 3: בקשת הלוואה דיגיטלית אוטומטית
    requestLoan: async (accountNumber, loanAmount) => {
        const response = await axios.post(`${API_BASE_URL}/${accountNumber}/loan`, null, {
            params: { loanAmount }
        });
        return response.data;
    }
};