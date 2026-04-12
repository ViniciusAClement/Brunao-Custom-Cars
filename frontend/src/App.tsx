import { useState } from 'react'
import './App.css'

interface Part {
  id: number;
  nome: string;
  marca: string;
  modelo: string;
  ano: number;
  preco: number;
  descricao: string;
}

interface Employee {
  id: number;
  nome: string;
  cargo: string;
  email: string;
  telefone: string;
}

interface Client {
  id: number;
  nome: string;
  email: string;
  telefone: string;
  endereco: string;
}

function App() {
  const [activeTab, setActiveTab] = useState<'parts' | 'employees' | 'clients'>('parts');

  const [parts, setParts] = useState<Part[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [clients, setClients] = useState<Client[]>([]);

  const [partForm, setPartForm] = useState<Omit<Part, 'id'>>({ nome: '', marca: '', modelo: '', ano: 0, preco: 0, descricao: '' });
  const [employeeForm, setEmployeeForm] = useState<Omit<Employee, 'id'>>({ nome: '', cargo: '', email: '', telefone: '' });
  const [clientForm, setClientForm] = useState<Omit<Client, 'id'>>({ nome: '', email: '', telefone: '', endereco: '' });

  const handleCreatePart = () => {
    if (partForm.nome && partForm.marca && partForm.modelo && partForm.ano && partForm.preco) {
      setParts([...parts, { ...partForm, id: Date.now() }]);
      setPartForm({ nome: '', marca: '', modelo: '', ano: 0, preco: 0, descricao: '' });
      alert('Peça criada com sucesso!');
    } else {
      alert('Preencha todos os campos obrigatórios.');
    }
  };

  const handleRegisterEmployee = () => {
    if (employeeForm.nome && employeeForm.cargo && employeeForm.email) {
      setEmployees([...employees, { ...employeeForm, id: Date.now() }]);
      setEmployeeForm({ nome: '', cargo: '', email: '', telefone: '' });
      alert('Funcionário cadastrado com sucesso!');
    } else {
      alert('Preencha todos os campos obrigatórios.');
    }
  };

  const handleRegisterClient = () => {
    if (clientForm.nome && clientForm.email) {
      setClients([...clients, { ...clientForm, id: Date.now() }]);
      setClientForm({ nome: '', email: '', telefone: '', endereco: '' });
      alert('Cliente cadastrado com sucesso!');
    } else {
      alert('Preencha todos os campos obrigatórios.');
    }
  };

  return (
    <div className="app">
      <header>
        <h1>Gerenciamento - Brunao Custom Cars</h1>
      </header>
      <nav>
        <button onClick={() => setActiveTab('parts')} className={activeTab === 'parts' ? 'active' : ''}>Criar Peças</button>
        <button onClick={() => setActiveTab('employees')} className={activeTab === 'employees' ? 'active' : ''}>Cadastrar Funcionário</button>
        <button onClick={() => setActiveTab('clients')} className={activeTab === 'clients' ? 'active' : ''}>Cadastrar Cliente</button>
      </nav>
      <main>
        {activeTab === 'parts' && (
          <div className="form-section">
            <h2>Criar Peça Automotiva</h2>
            <form onSubmit={(e) => { e.preventDefault(); handleCreatePart(); }}>
              <label>
                Nome:
                <input type="text" value={partForm.nome} onChange={(e) => setPartForm({ ...partForm, nome: e.target.value })} required />
              </label>
              <label>
                Marca:
                <input type="text" value={partForm.marca} onChange={(e) => setPartForm({ ...partForm, marca: e.target.value })} required />
              </label>
              <label>
                Modelo:
                <input type="text" value={partForm.modelo} onChange={(e) => setPartForm({ ...partForm, modelo: e.target.value })} required />
              </label>
              <label>
                Ano:
                <input type="number" value={partForm.ano} onChange={(e) => setPartForm({ ...partForm, ano: parseInt(e.target.value) })} required />
              </label>
              <label>
                Preço:
                <input type="number" step="0.01" value={partForm.preco} onChange={(e) => setPartForm({ ...partForm, preco: parseFloat(e.target.value) })} required />
              </label>
              <label>
                Descrição:
                <textarea value={partForm.descricao} onChange={(e) => setPartForm({ ...partForm, descricao: e.target.value })} />
              </label>
              <button type="submit">Criar Peça</button>
            </form>
            <h3>Peças Criadas</h3>
            <ul>
              {parts.map(part => (
                <li key={part.id}>{part.nome} - {part.marca} {part.modelo} ({part.ano}) - R$ {part.preco}</li>
              ))}
            </ul>
          </div>
        )}
        {activeTab === 'employees' && (
          <div className="form-section">
            <h2>Cadastrar Funcionário</h2>
            <form onSubmit={(e) => { e.preventDefault(); handleRegisterEmployee(); }}>
              <label>
                Nome:
                <input type="text" value={employeeForm.nome} onChange={(e) => setEmployeeForm({ ...employeeForm, nome: e.target.value })} required />
              </label>
              <label>
                Cargo:
                <input type="text" value={employeeForm.cargo} onChange={(e) => setEmployeeForm({ ...employeeForm, cargo: e.target.value })} required />
              </label>
              <label>
                Email:
                <input type="email" value={employeeForm.email} onChange={(e) => setEmployeeForm({ ...employeeForm, email: e.target.value })} required />
              </label>
              <label>
                Telefone:
                <input type="tel" value={employeeForm.telefone} onChange={(e) => setEmployeeForm({ ...employeeForm, telefone: e.target.value })} />
              </label>
              <button type="submit">Cadastrar Funcionário</button>
            </form>
            <h3>Funcionários Cadastrados</h3>
            <ul>
              {employees.map(emp => (
                <li key={emp.id}>{emp.nome} - {emp.cargo} - {emp.email}</li>
              ))}
            </ul>
          </div>
        )}
        {activeTab === 'clients' && (
          <div className="form-section">
            <h2>Cadastrar Cliente</h2>
            <form onSubmit={(e) => { e.preventDefault(); handleRegisterClient(); }}>
              <label>
                Nome:
                <input type="text" value={clientForm.nome} onChange={(e) => setClientForm({ ...clientForm, nome: e.target.value })} required />
              </label>
              <label>
                Email:
                <input type="email" value={clientForm.email} onChange={(e) => setClientForm({ ...clientForm, email: e.target.value })} required />
              </label>
              <label>
                Telefone:
                <input type="tel" value={clientForm.telefone} onChange={(e) => setClientForm({ ...clientForm, telefone: e.target.value })} />
              </label>
              <label>
                Endereço:
                <textarea value={clientForm.endereco} onChange={(e) => setClientForm({ ...clientForm, endereco: e.target.value })} />
              </label>
              <button type="submit">Cadastrar Cliente</button>
            </form>
            <h3>Clientes Cadastrados</h3>
            <ul>
              {clients.map(client => (
                <li key={client.id}>{client.nome} - {client.email}</li>
              ))}
            </ul>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
