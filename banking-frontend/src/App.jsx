import React, { useState } from 'react';
import { accountService } from './services/api';
import './App.css'; // פה תוכלו לבקש מקופיילוט לעשות לכן עיצוב משגע עזורי/בנקאי מודרני

function App() {
  const [accountNumber, setAccountNumber] = useState('');
  const [accountData, setAccountData] = useState(null);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // שדות עבור הטפסים השונים במערכת
  const [createForm, setCreateForm] = useState({ accountNumber: '', username: '', email: '', balance: 0 });
  const [transferForm, setTransferForm] = useState({ destAccount: '', amount: '' });
  const [actionForm, setActionForm] = useState({ type: 'DEPOSIT', amount: '' });
  const [loanAmount, setLoanAmount] = useState('');

  // פונקציה לשליפת נתוני החשבון המעודכנים מהשרת
  const fetchAccountInfo = async (numberToFetch = accountNumber) => {
    try {
      setError('');
      const data = await accountService.getAccount(numberToFetch);
      setAccountData(data);
    } catch (err) {
      setError(err.response?.data?.message || 'החשבון לא נמצא במערכת');
      setAccountData(null);
    }
  };

  // פונקציה לפתיחת חשבון חדש
  const handleCreateAccount = async (e) => {
    e.preventDefault();
    try {
      setError('');
      await accountService.createAccount(createForm);
      setSuccessMessage('החשבון והמשתמש נוצרו בהצלחה!');
      setAccountNumber(createForm.accountNumber);
      fetchAccountInfo(createForm.accountNumber);
    } catch (err) {
      setError('שגיאה ביצירת החשבון');
    }
  };

  // פונקציה לביצוע העברת כספים (פונקציה מעניינת 1)
  const handleTransfer = async (e) => {
    e.preventDefault();
    try {
      setError('');
      const res = await accountService.transferMoney(accountNumber, transferForm.destAccount, transferForm.amount);
      setSuccessMessage(res);
      fetchAccountInfo(); // רענון הנתונים והטבלה
    } catch (err) {
      setError(err.response?.data?.message || 'ההעברה נכשלה');
    }
  };

  // פונקציה להפקדה / משיכה (פונקציה מעניינת 2)
  const handleAction = async (e) => {
    e.preventDefault();
    try {
      setError('');
      await accountService.updateBalance(accountNumber, actionForm.type, actionForm.amount);
      setSuccessMessage('הפעולה בוצעה בהצלחה!');
      fetchAccountInfo();
    } catch (err) {
      setError(err.response?.data?.message || 'הפעולה נכשלה');
    }
  };

  // פונקציה לבקשת הלוואה (פונקציה מעניינת 3)
  const handleLoan = async (e) => {
    e.preventDefault();
    try {
      setError('');
      await accountService.requestLoan(accountNumber, loanAmount);
      setSuccessMessage('בקשת ההלוואה אושרה והכסף הופקד בחשבון!');
      fetchAccountInfo();
    } catch (err) {
      setError(err.response?.data?.message || 'בקשת ההלוואה נדחתה על ידי מערכת הבנק');
    }
  };

  return (
    <div style={{ direction: 'rtl', padding: '20px', fontFamily: 'Arial, sans-serif', maxWidth: '900px', margin: '0 auto' }}>
      <h1>🏦 מערכת הבנק הדיגיטלי - פרויקט גמר</h1>
      
      {error && <div style={{ color: 'red', background: '#ffe6e6', padding: '10px', borderRadius: '5px', marginBottom: '10px' }}>{error}</div>}
      {successMessage && <div style={{ color: 'green', background: '#e6ffe6', padding: '10px', borderRadius: '5px', marginBottom: '10px' }}>{successMessage}</div>}

      {/* אזור 1: פתיחת חשבון חדש */}
      <section style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>🆕 פתיחת חשבון בנק ומשתמש חדש</h3>
        <form onSubmit={handleCreateAccount} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          <input type="text" placeholder="מספר חשבון" required onChange={e => setCreateForm({...createForm, accountNumber: e.target.value})} />
          <input type="text" placeholder="שם מלא" required onChange={e => setCreateForm({...createForm, username: e.target.value})} />
          <input type="email" placeholder="אימייל" required onChange={e => setCreateForm({...createForm, email: e.target.value})} />
          <input type="number" placeholder="הפקדה ראשונית" required onChange={e => setCreateForm({...createForm, balance: parseFloat(e.target.value)})} />
          <button type="submit">פתח חשבון</button>
        </form>
      </section>

      {/* אזור 2: כניסה לחשבון קיים */}
      <section style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>🔑 כניסה לחשבון מנוהל</h3>
        <div style={{ display: 'flex', gap: '10px' }}>
          <input type="text" placeholder="הקלידו מספר חשבון" value={accountNumber} onChange={e => setAccountNumber(e.target.value)} />
          <button onClick={() => fetchAccountInfo()}>צפה בחשבון</button>
        </div>
      </section>

      {/* אם המשתמש נכנס לחשבון - נציג את כל הפעולות והמידע */}
      {accountData && (
        <div>
          {/* תצוגת פרטי חשבון נוכחי */}
          <div style={{ background: '#f0f4f8', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
            <h2>שלום, {accountData.username} 👋</h2>
            <p>📧 אימייל: {accountData.email} | 💳 מספר חשבון: <strong>{accountData.accountNumber}</strong></p>
            <h1 style={{ color: '#0056b3' }}>יתרה נוכחית: ₪{accountData.balance}</h1>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '15px', marginBottom: '20px' }}>
            {/* פונקציה מעניינת 1: העברה */}
            <div style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '8px' }}>
              <h4>💸 העברת כספים</h4>
              <form onSubmit={handleTransfer}>
                <input type="text" placeholder="חשבון יעד" required style={{ width: '90%', marginBottom: '5px' }} onChange={e => setTransferForm({...transferForm, destAccount: e.target.value})} />
                <input type="number" placeholder="סכום להעברה" required style={{ width: '90%', marginBottom: '5px' }} onChange={e => setTransferForm({...transferForm, amount: parseFloat(e.target.value)})} />
                <button type="submit" style={{ width: '95%' }}>בצע העברה</button>
              </form>
            </div>

            {/* פונקציה מעניינת 2: הפקדה/משיכה */}
            <div style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '8px' }}>
              <h4>🏧 פעולות בכספומט</h4>
              <form onSubmit={handleAction}>
                <select style={{ width: '95%', marginBottom: '5px', padding: '4px' }} onChange={e => setActionForm({...actionForm, type: e.target.value})}>
                  <option value="DEPOSIT">הפקדת מזומן</option>
                  <option value="WITHDRAW">משיכת מזומן</option>
                </select>
                <input type="number" placeholder="סכום" required style={{ width: '90%', marginBottom: '5px' }} onChange={e => setActionForm({...actionForm, amount: parseFloat(e.target.value)})} />
                <button type="submit" style={{ width: '95%' }}>בצע פעולה</button>
              </form>
            </div>

            {/* פונקציה מעניינת 3: בקשת הלוואה */}
            <div style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '8px', background: '#fff9e6' }}>
              <h4>🤖 אישור הלוואה אוטומטי</h4>
              <p style={{ fontSize: '12px', color: '#666' }}>*אישור מיידי עד פי 3 מהיתרה הנוכחית שלך בחשבון!</p>
              <form onSubmit={handleLoan}>
                <input type="number" placeholder="סכום הלוואה מבוקש" required style={{ width: '90%', marginBottom: '5px' }} value={loanAmount} onChange={e => setLoanAmount(e.target.value)} />
                <button type="submit" style={{ width: '95%', background: '#ffc107', border: 'none', cursor: 'pointer', padding: '6px', borderRadius: '4px' }}>בקש הלוואה</button>
              </form>
            </div>
          </div>

          {/* טבלת היסטוריית פעולות המנוהלת בבסיס הנתונים */}
          <h3>📜 היסטוריית פעולות ותנועות בחשבון</h3>
          <table border="1" cellPadding="8" style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
            <thead>
              <tr style={{ background: '#eee' }}>
                <th>סוג הפעולה</th>
                <th>סכום הפעולה</th>
                <th>תאריך ושעה</th>
              </tr>
            </thead>
            <tbody>
              {accountData.transactions && accountData.transactions.length > 0 ? (
                accountData.transactions.map((tx, idx) => (
                  <tr key={idx}>
                    <td style={{ fontWeight: 'bold', color: tx.operationType.includes('OUT') || tx.operationType === 'WITHDRAW' ? 'red' : 'green' }}>
                      {tx.operationType}
                    </td>
                    <td>₪{tx.amount}</td>
                    <td>{new Date(tx.timestamp).toLocaleString('he-IL')}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="3">לא בוצעו תנועות בחשבון זה עדיין</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default App;