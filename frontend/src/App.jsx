import { useEffect, useState } from 'react'
import './App.css'
import { apiFetch, setAccessTokenGetter } from './api'

const AUTH_STORAGE_KEY = 'bcc_auth'

function readStoredAuth() {
  try {
    const raw = sessionStorage.getItem(AUTH_STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (parsed?.token && parsed?.email != null) return parsed
  } catch {
    /* ignore */
  }
  return null
}

function writeStoredAuth(payload) {
  sessionStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(payload))
}

function clearStoredAuth() {
  sessionStorage.removeItem(AUTH_STORAGE_KEY)
}

function initialPageFromStored() {
  const s = readStoredAuth()
  if (!s?.token) return 'home'
  const r = String(s.role ?? '').toUpperCase()
  if (r === 'GERENTE') return 'admin-dashboard'
  if (r === 'FUNCIONARIO') return 'employee-dashboard'
  return 'client-dashboard'
}

function initialAuthFromStored() {
  const s = readStoredAuth()
  if (!s?.token) return { token: null, email: null, role: null }
  return { token: s.token, email: s.email ?? null, role: s.role ?? null }
}

const HomePage = ({ setCurrentPage, parts, categories, cars }) => (
  <div className="home-page">
    <header className="navbar">
      <div className="navbar-left">
        <div className="logo">🏁 Brunão Custom Cars</div>
        <input type="text" className="search-bar" placeholder="Buscar peças de carro, produtos automotivos, etc." />
      </div>
      <div className="navbar-right">
        <a href="#" onClick={() => setCurrentPage('catalog')}>PÓS</a>
        <a href="#" onClick={() => setCurrentPage('catalog')}>ANÚNCIOS</a>
        <button onClick={() => setCurrentPage('login')} className="btn-login">Entrar</button>
        <button onClick={() => setCurrentPage('register')} className="btn-register">Criar conta</button>
        <a href="#" className="cart-icon">🛒</a>
      </div>
    </header>

    <div className="banner">
      <div className="banner-content">
        <div className="banner-logo">🚗 CUSTOM CARS</div>
        <h1>MONTE SEU PROJETINHO NA BRUNÃO!</h1>
        <div className="banner-links">
          <a href="#" onClick={() => setCurrentPage('catalog')}>CATÁLOGO</a>
          <span>|</span>
          <a href="#" onClick={() => setCurrentPage('catalog')}>OFERTAS</a>
        </div>
      </div>
    </div>

    <section className="melhores-ofertas">
      <h2>MELHORES OFERTAS 🔥</h2>
      <div className="products-grid">
        {parts.length === 0 ? (
          <div className="no-products">
            <p>Nenhum anúncio cadastrado ainda.</p>
            <p>Faça login como gerente para cadastrar peças!</p>
          </div>
        ) : (
          parts.map(part => (
            <div key={part.id} className="product-card">
              <div className="product-image">
                <img src="https://via.placeholder.com/150x150?text=Peça" alt={part.name} />
              </div>
              <h3>{part.name}</h3>
              <p className="product-description">{part.description}</p>
              <p className="product-price">R$ {Number(part.price).toFixed(2)}</p>
              {(part.categoryIds || []).length > 0 && (
                <p className="product-tags">Categorias: {(part.categoryIds || []).map(id => categories.find(cat => cat.id === id)?.name).filter(Boolean).join(', ')}</p>
              )}
              {(part.carIds || []).length > 0 && (
                <p className="product-tags">Compatível com: {(part.carIds || []).map(id => {
                  const car = cars.find(carItem => carItem.id === id)
                  return car ? `${car.carBrandName} ${car.nome} ${car.ano}` : null
                }).filter(Boolean).join(', ')}</p>
              )}
              <button className="btn-add-cart">Adicionar ao carrinho</button>
            </div>
          ))
        )}
      </div>
    </section>
  </div>
)

const LoginPage = ({ setCurrentPage, loginEmail, setLoginEmail, loginPassword, setLoginPassword, loginError, loginSubmitting, handleLogin }) => (
  <div className="auth-page">
    <div className="auth-card">
      <h2>ENTRAR</h2>
      {loginError && <p className="error-message">{loginError}</p>}
      <form
        onSubmit={async (e) => {
          e.preventDefault()
          await handleLogin(loginEmail, loginPassword)
        }}
      >
        <label>Email</label>
        <input 
          type="email" 
          value={loginEmail}
          onChange={(e) => setLoginEmail(e.target.value)}
          required 
          disabled={loginSubmitting}
        />
        <label>Senha</label>
        <input 
          type="password" 
          value={loginPassword}
          onChange={(e) => setLoginPassword(e.target.value)}
          required 
          disabled={loginSubmitting}
        />
        <button type="submit" className="btn-submit" disabled={loginSubmitting}>
          {loginSubmitting ? 'Entrando…' : 'Entrar'}
        </button>
      </form>
      <p className="login-hint">Conta demo (API): <br/><strong>gerente@empresa.com</strong> / <strong>senha123</strong></p>
      <p className="auth-link" onClick={() => setCurrentPage('home')}>← Voltar</p>
    </div>
  </div>
)

const RegisterPage = ({ setCurrentPage, registerForm, setRegisterForm, handleRegister }) => (
  <div className="register-page">
    <div className="register-card">
      <h2>CADASTRE-SE</h2>
      <form onSubmit={handleRegister}>
        <input 
          type="text" 
          placeholder="NOME" 
          value={registerForm.nome}
          onChange={(e) => setRegisterForm({ ...registerForm, nome: e.target.value })}
          required 
        />
        <input 
          type="text" 
          placeholder="SOBRENOME" 
          value={registerForm.sobrenome}
          onChange={(e) => setRegisterForm({ ...registerForm, sobrenome: e.target.value })}
          required 
        />
        <input 
          type="text" 
          placeholder="CPF" 
          value={registerForm.cpf}
          onChange={(e) => setRegisterForm({ ...registerForm, cpf: e.target.value })}
        />
        <input 
          type="email" 
          placeholder="EMAIL" 
          value={registerForm.email}
          onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
          required 
        />
        <input 
          type="tel" 
          placeholder="CELULAR/WHATSAPP" 
          value={registerForm.celular}
          onChange={(e) => setRegisterForm({ ...registerForm, celular: e.target.value })}
          required 
        />
        <input 
          type="text" 
          placeholder="ENDEREÇO" 
          value={registerForm.endereco}
          onChange={(e) => setRegisterForm({ ...registerForm, endereco: e.target.value })}
        />
        <input 
          type="text" 
          placeholder="CIDADE" 
          value={registerForm.cidade}
          onChange={(e) => setRegisterForm({ ...registerForm, cidade: e.target.value })}
        />
        <input 
          type="text" 
          placeholder="BAIRRO" 
          value={registerForm.bairro}
          onChange={(e) => setRegisterForm({ ...registerForm, bairro: e.target.value })}
        />
        <input 
          type="text" 
          placeholder="RUA" 
          value={registerForm.rua}
          onChange={(e) => setRegisterForm({ ...registerForm, rua: e.target.value })}
        />
        <input 
          type="text" 
          placeholder="NÚMERO" 
          value={registerForm.numero}
          onChange={(e) => setRegisterForm({ ...registerForm, numero: e.target.value })}
        />
        <input 
          type="text" 
          placeholder="COMPLEMENTO" 
          value={registerForm.complemento}
          onChange={(e) => setRegisterForm({ ...registerForm, complemento: e.target.value })}
        />
        <label className="checkbox">
          <input type="checkbox" required />
          Lei Funcionário uma "Brunão Custom Cars"
        </label>
        <button type="submit" className="btn-submit">CADASTRAR-SE</button>
      </form>
      <p className="auth-link" onClick={() => setCurrentPage('home')}>← Voltar</p>
    </div>
  </div>
)

const AdminDashboard = ({ setCurrentPage, handleLogout, parts, newPartForm, setNewPartForm, handleCreatePart, clients, editingPart, handleEditPart, handleDeletePart, handleCancelEditPart, categories, cars, searchQuery, setSearchQuery, selectedCategoryFilter, setSelectedCategoryFilter, selectedBrandFilter, setSelectedBrandFilter, selectedCarFilter, setSelectedCarFilter }) => (
  <div className="admin-layout">
    <aside className="sidebar">
      <div className="sidebar-logo">⚙️ Gerência</div>
      <nav className="sidebar-menu">
        <button className="menu-item active">Gestão de Peças</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-categories')}>Categorias</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-brands')}>Marcas</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-cars')}>Veículos</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-employees')}>Funcionários</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-clients')}>Clientes</button>
        <button className="menu-item">Relatórios</button>
        <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
      </nav>
    </aside>

    <main className="admin-main">
      <div className="admin-top">
        <h1>Bem-vindo, Gerente!</h1>
        <div className="stats">
          <div className="stat-card">
            <p className="stat-value">R$ {(parts.reduce((acc, p) => acc + (Number(p.price) || 0), 0)).toFixed(2)}</p>
            <p className="stat-label">Vendas últimas 7 dias</p>
            <span className="stat-percent">↑ 0%</span>
          </div>
          <div className="stat-card">
            <p className="stat-value">{parts.length}</p>
            <p className="stat-label">Peças Cadastradas</p>
          </div>
          <div className="stat-card">
            <p className="stat-value">{clients.length}</p>
            <p className="stat-label">Clientes Cadastrados</p>
          </div>
        </div>
      </div>

      <div className="admin-content">
        <section className="admin-section">
          <h2>Gestão de Peças</h2>
          
          <div className="create-part-form">
            <h3>{editingPart ? 'Editar Peça' : 'Cadastrar Nova Peça'}</h3>
            <form onSubmit={(e) => { e.preventDefault(); handleCreatePart(); }}>
                      <input 
                type="text" 
                placeholder="Nome da peça" 
                value={newPartForm.name}
                onChange={(e) => setNewPartForm({ ...newPartForm, name: e.target.value })}
                required 
              />
              <textarea
                placeholder="Descrição da peça"
                value={newPartForm.description}
                onChange={(e) => setNewPartForm({ ...newPartForm, description: e.target.value })}
                required
              />
              <input 
                type="number" 
                step="0.01"
                placeholder="Preço" 
                value={newPartForm.price}
                onChange={(e) => setNewPartForm({ ...newPartForm, price: e.target.value })}
                required 
              />
              <select
                multiple
                value={newPartForm.categoryIds}
                onChange={(e) => setNewPartForm({
                  ...newPartForm,
                  categoryIds: Array.from(e.target.selectedOptions, option => Number(option.value))
                })}
                required
              >
                <option value="" disabled>Selecione categorias</option>
                {categories.map(category => (
                  <option key={category.id} value={category.id}>{category.name}</option>
                ))}
              </select>
              <select
                multiple
                value={newPartForm.carIds}
                onChange={(e) => setNewPartForm({
                  ...newPartForm,
                  carIds: Array.from(e.target.selectedOptions, option => Number(option.value))
                })}
                required
              >
                <option value="" disabled>Selecione veículos compatíveis</option>
                {cars.map(car => (
                  <option key={car.id} value={car.id}>{car.carBrandName} {car.nome} ({car.ano})</option>
                ))}
              </select>
              <div className="form-buttons">
                <button type="submit" className="btn-submit-form">{editingPart ? 'Atualizar' : 'Criar Peça'}</button>
                {editingPart && (
                  <button type="button" className="btn-cancel" onClick={handleCancelEditPart}>Cancelar</button>
                )}
              </div>
            </form>
          </div>

          <div className="admin-search">
            <input
              type="text"
              placeholder="Buscar por nome ou descrição"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <select value={selectedCategoryFilter} onChange={(e) => setSelectedCategoryFilter(e.target.value)}>
              <option value="">Categoria</option>
              {categories.map(category => (
                <option key={category.id} value={String(category.id)}>{category.name}</option>
              ))}
            </select>
            <select value={selectedBrandFilter} onChange={(e) => setSelectedBrandFilter(e.target.value)}>
              <option value="">Marca</option>
              {[...new Set(cars.map(car => car.carBrandName))].map(brand => (
                <option key={brand} value={brand}>{brand}</option>
              ))}
            </select>
            <select value={selectedCarFilter} onChange={(e) => setSelectedCarFilter(e.target.value)}>
              <option value="">Veículo Compatível</option>
              {cars.map(car => (
                <option key={car.id} value={String(car.id)}>{`${car.carBrandName} ${car.nome} ${car.ano}`}</option>
              ))}
            </select>
            <button className="btn-search" type="button" onClick={() => { setSearchQuery(searchQuery); }}>
              Buscar
            </button>
          </div>

          <div className="parts-list">
            {parts.length === 0 ? (
              <p style={{ textAlign: 'center', color: '#999', padding: '20px' }}>Nenhuma peça cadastrada. Clique em "Criar Peça" para começar.</p>
            ) : (
              parts.map(part => (
                <div key={part.id} className="part-item">
                  <img src="https://via.placeholder.com/100x100?text=Peça" alt={part.name} />
                  <div className="part-info">
                    <h3>{part.name}</h3>
                    <p className="part-description">{part.description}</p>
                    <div className="part-meta">
                      <span>{(part.categoryIds || []).map(id => categories.find(cat => cat.id === id)?.name).filter(Boolean).join(', ')}</span>
                      <span>{(part.carIds || []).map(id => cars.find(car => car.id === id)?.carBrandName + ' ' + cars.find(car => car.id === id)?.nome + ' ' + (cars.find(car => car.id === id)?.ano ?? '')).filter(Boolean).join(' | ')}</span>
                    </div>
                    <div className="part-prices">
                      <span>R$ {Number(part.price).toFixed(2)}</span>
                    </div>
                  </div>
                      <div className="part-actions">
                    <button className="btn-edit" onClick={() => handleEditPart(part)}>✏️ Editar</button>
                    <button className="btn-delete" onClick={() => handleDeletePart(part.id)}>🗑️ Remover</button>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>

        <aside className="admin-side">
          <div className="area-gerencia">
            <h2>Área de Gerência</h2>
            <div className="gerencia-card">
              <h3>👤 Gerenciar Funcionários</h3>
              <p>Cadastre o gerenciador funcionários do sistema</p>
              <button className="btn-gerencia" onClick={() => setCurrentPage('admin-employees')}>Gerenciar</button>
            </div>
            <div className="gerencia-card">
              <h3>👥 Gerenciar Clientes</h3>
              <p>Controle o dados dos clientes cadastrados</p>
              <button className="btn-gerencia" onClick={() => setCurrentPage('admin-clients')}>Gerenciar</button>
            </div>
            <div className="gerencia-card">
              <h3>📊 Gerar Relatórios</h3>
              <p>Veja relatórios de vendas e outras métricas</p>
              <button className="btn-gerencia">Gerar</button>
            </div>
          </div>
        </aside>
      </div>
    </main>
  </div>
)

const AdminEmployees = ({ setCurrentPage, handleLogout, employees, newEmployeeForm, setNewEmployeeForm, handleCreateEmployee, editingEmployee, handleEditEmployee, handleDeleteEmployee, handleCancelEditEmployee }) => (
  <div className="admin-layout">
    <aside className="sidebar">
      <div className="sidebar-logo">⚙️ Gerência</div>
      <nav className="sidebar-menu">
        <button className="menu-item" onClick={() => setCurrentPage('admin-dashboard')}>Gestão de Peças</button>
        <button className="menu-item active">Funcionários</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-clients')}>Clientes</button>
        <button className="menu-item">Relatórios</button>
        <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
      </nav>
    </aside>

    <main className="admin-main">
      <div className="form-container">
        <h2>{editingEmployee ? 'Editar Funcionário' : 'Cadastrar Funcionário'}</h2>
        <form className="admin-form" onSubmit={handleCreateEmployee}>
          <input 
            type="text" 
            placeholder="Nome completo" 
            value={newEmployeeForm.nome}
            onChange={(e) => setNewEmployeeForm({ ...newEmployeeForm, nome: e.target.value })}
            required 
          />
          <input 
            type="text" 
            placeholder="Cargo" 
            value={newEmployeeForm.cargo}
            onChange={(e) => setNewEmployeeForm({ ...newEmployeeForm, cargo: e.target.value })}
            required 
          />
          <input 
            type="email" 
            placeholder="Email" 
            value={newEmployeeForm.email}
            onChange={(e) => setNewEmployeeForm({ ...newEmployeeForm, email: e.target.value })}
            required 
          />
          <input 
            type="tel" 
            placeholder="Telefone" 
            value={newEmployeeForm.telefone}
            onChange={(e) => setNewEmployeeForm({ ...newEmployeeForm, telefone: e.target.value })}
          />
          <div className="form-buttons">
            <button type="submit" className="btn-submit">{editingEmployee ? 'Atualizar' : 'Cadastrar'}</button>
            {editingEmployee && (
              <button type="button" className="btn-cancel" onClick={handleCancelEditEmployee}>Cancelar</button>
            )}
          </div>
        </form>

        <h3>Funcionários Cadastrados</h3>
        <div className="list-container">
          {employees.length === 0 ? (
            <p>Nenhum funcionário cadastrado</p>
          ) : (
            employees.map(emp => (
              <div key={emp.id} className="list-item">
                <div className="list-info">
                  <span>{emp.nome} - {emp.cargo}</span>
                  <small>{emp.email} {emp.telefone && `| ${emp.telefone}`}</small>
                </div>
                <div className="list-actions">
                  <button className="btn-edit" onClick={() => handleEditEmployee(emp)}>✏️ Editar</button>
                  <button className="btn-delete" onClick={() => handleDeleteEmployee(emp.id)}>🗑️ Excluir</button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  </div>
)

const AdminClients = ({ setCurrentPage, handleLogout, clients, newClientForm, setNewClientForm, handleCreateClient, editingClient, handleEditClient, handleDeleteClient, handleCancelEditClient }) => (
  <div className="admin-layout">
    <aside className="sidebar">
      <div className="sidebar-logo">⚙️ Gerência</div>
      <nav className="sidebar-menu">
        <button className="menu-item" onClick={() => setCurrentPage('admin-dashboard')}>Gestão de Peças</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-employees')}>Funcionários</button>
        <button className="menu-item active">Clientes</button>
        <button className="menu-item">Relatórios</button>
        <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
      </nav>
    </aside>

    <main className="admin-main">
      <div className="form-container">
        <h2>{editingClient ? 'Editar Cliente' : 'Cadastrar Cliente'}</h2>
        <form className="admin-form" onSubmit={handleCreateClient}>
          <input 
            type="text" 
            placeholder="Nome completo" 
            value={newClientForm.nome}
            onChange={(e) => setNewClientForm({ ...newClientForm, nome: e.target.value })}
            required 
          />
          <input 
            type="email" 
            placeholder="Email" 
            value={newClientForm.email}
            onChange={(e) => setNewClientForm({ ...newClientForm, email: e.target.value })}
            required 
          />
          <input 
            type="tel" 
            placeholder="Telefone" 
            value={newClientForm.telefone}
            onChange={(e) => setNewClientForm({ ...newClientForm, telefone: e.target.value })}
          />
          <textarea 
            placeholder="Endereço"
            value={newClientForm.endereco}
            onChange={(e) => setNewClientForm({ ...newClientForm, endereco: e.target.value })}
          ></textarea>
          <div className="form-buttons">
            <button type="submit" className="btn-submit">{editingClient ? 'Atualizar' : 'Cadastrar'}</button>
            {editingClient && (
              <button type="button" className="btn-cancel" onClick={handleCancelEditClient}>Cancelar</button>
            )}
          </div>
        </form>

        <h3>Clientes Cadastrados</h3>
        <div className="list-container">
          {clients.length === 0 ? (
            <p>Nenhum cliente cadastrado</p>
          ) : (
            clients.map(client => (
              <div key={client.id} className="list-item">
                <div className="list-info">
                  <span>{client.nome}</span>
                  <small>{client.email} {client.telefone && `| ${client.telefone}`}</small>
                  {client.endereco && <small>📍 {client.endereco}</small>}
                </div>
                <div className="list-actions">
                  <button className="btn-edit" onClick={() => handleEditClient(client)}>✏️ Editar</button>
                  <button className="btn-delete" onClick={() => handleDeleteClient(client.id)}>🗑️ Excluir</button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  </div>
)

const AdminCategories = ({ setCurrentPage, handleLogout, categories, newCategoryForm, setNewCategoryForm, handleCreateCategory, editingCategory, handleEditCategory, handleDeleteCategory, handleCancelCategory }) => (
  <div className="admin-layout">
    <aside className="sidebar">
      <div className="sidebar-logo">⚙️ Gerência</div>
      <nav className="sidebar-menu">
        <button className="menu-item" onClick={() => setCurrentPage('admin-dashboard')}>Gestão de Peças</button>
        <button className="menu-item active">Categorias</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-brands')}>Marcas</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-cars')}>Veículos</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-employees')}>Funcionários</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-clients')}>Clientes</button>
        <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
      </nav>
    </aside>

    <main className="admin-main">
      <div className="form-container">
        <h2>{editingCategory ? 'Editar Categoria' : 'Cadastrar Categoria'}</h2>
        <form className="admin-form" onSubmit={handleCreateCategory}>
          <input
            type="text"
            placeholder="Nome da categoria"
            value={newCategoryForm.name}
            onChange={(e) => setNewCategoryForm({ ...newCategoryForm, name: e.target.value })}
            required
          />
          <div className="form-buttons">
            <button type="submit" className="btn-submit">{editingCategory ? 'Atualizar' : 'Cadastrar'}</button>
            {editingCategory && (
              <button type="button" className="btn-cancel" onClick={handleCancelCategory}>Cancelar</button>
            )}
          </div>
        </form>

        <h3>Categorias Cadastradas</h3>
        <div className="list-container">
          {categories.length === 0 ? (
            <p>Nenhuma categoria cadastrada</p>
          ) : (
            categories.map(category => (
              <div key={category.id} className="list-item">
                <div className="list-info">
                  <span>{category.name}</span>
                </div>
                <div className="list-actions">
                  <button className="btn-edit" onClick={() => handleEditCategory(category)}>✏️ Editar</button>
                  <button className="btn-delete" onClick={() => handleDeleteCategory(category.id)}>🗑️ Excluir</button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  </div>
)

const AdminBrands = ({ setCurrentPage, handleLogout, carBrands, newBrandForm, setNewBrandForm, handleCreateBrand, editingBrand, handleEditBrand, handleDeleteBrand, handleCancelBrand }) => (
  <div className="admin-layout">
    <aside className="sidebar">
      <div className="sidebar-logo">⚙️ Gerência</div>
      <nav className="sidebar-menu">
        <button className="menu-item" onClick={() => setCurrentPage('admin-dashboard')}>Gestão de Peças</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-categories')}>Categorias</button>
        <button className="menu-item active">Marcas</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-cars')}>Veículos</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-employees')}>Funcionários</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-clients')}>Clientes</button>
        <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
      </nav>
    </aside>

    <main className="admin-main">
      <div className="form-container">
        <h2>{editingBrand ? 'Editar Marca' : 'Cadastrar Marca'}</h2>
        <form className="admin-form" onSubmit={handleCreateBrand}>
          <input
            type="text"
            placeholder="Nome da marca"
            value={newBrandForm.name}
            onChange={(e) => setNewBrandForm({ ...newBrandForm, name: e.target.value })}
            required
          />
          <div className="form-buttons">
            <button type="submit" className="btn-submit">{editingBrand ? 'Atualizar' : 'Cadastrar'}</button>
            {editingBrand && (
              <button type="button" className="btn-cancel" onClick={handleCancelBrand}>Cancelar</button>
            )}
          </div>
        </form>

        <h3>Marcas Cadastradas</h3>
        <div className="list-container">
          {carBrands.length === 0 ? (
            <p>Nenhuma marca cadastrada</p>
          ) : (
            carBrands.map(brand => (
              <div key={brand.id} className="list-item">
                <div className="list-info">
                  <span>{brand.name}</span>
                </div>
                <div className="list-actions">
                  <button className="btn-edit" onClick={() => handleEditBrand(brand)}>✏️ Editar</button>
                  <button className="btn-delete" onClick={() => handleDeleteBrand(brand.id)}>🗑️ Excluir</button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  </div>
)

const AdminCars = ({ setCurrentPage, handleLogout, cars, carBrands, newCarForm, setNewCarForm, handleCreateCar, editingCar, handleEditCar, handleDeleteCar, handleCancelCar }) => (
  <div className="admin-layout">
    <aside className="sidebar">
      <div className="sidebar-logo">⚙️ Gerência</div>
      <nav className="sidebar-menu">
        <button className="menu-item" onClick={() => setCurrentPage('admin-dashboard')}>Gestão de Peças</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-categories')}>Categorias</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-brands')}>Marcas</button>
        <button className="menu-item active">Veículos</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-employees')}>Funcionários</button>
        <button className="menu-item" onClick={() => setCurrentPage('admin-clients')}>Clientes</button>
        <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
      </nav>
    </aside>

    <main className="admin-main">
      <div className="form-container">
        <h2>{editingCar ? 'Editar Veículo' : 'Cadastrar Veículo'}</h2>
        <form className="admin-form" onSubmit={handleCreateCar}>
          <input
            type="text"
            placeholder="Modelo do veículo"
            value={newCarForm.nome}
            onChange={(e) => setNewCarForm({ ...newCarForm, nome: e.target.value })}
            required
          />
          <input
            type="number"
            placeholder="Ano"
            value={newCarForm.ano}
            onChange={(e) => setNewCarForm({ ...newCarForm, ano: e.target.value })}
            required
          />
          <select
            value={newCarForm.carBrandId}
            onChange={(e) => setNewCarForm({ ...newCarForm, carBrandId: e.target.value })}
            required
          >
            <option value="">Escolha a marca</option>
            {carBrands.map(brand => (
              <option key={brand.id} value={String(brand.id)}>{brand.name}</option>
            ))}
          </select>
          <div className="form-buttons">
            <button type="submit" className="btn-submit">{editingCar ? 'Atualizar' : 'Cadastrar'}</button>
            {editingCar && (
              <button type="button" className="btn-cancel" onClick={handleCancelCar}>Cancelar</button>
            )}
          </div>
        </form>

        <h3>Veículos Cadastrados</h3>
        <div className="list-container">
          {cars.length === 0 ? (
            <p>Nenhum veículo cadastrado</p>
          ) : (
            cars.map(car => (
              <div key={car.id} className="list-item">
                <div className="list-info">
                  <span>{car.carBrandName} {car.nome} {car.ano}</span>
                </div>
                <div className="list-actions">
                  <button className="btn-edit" onClick={() => handleEditCar(car)}>✏️ Editar</button>
                  <button className="btn-delete" onClick={() => handleDeleteCar(car.id)}>🗑️ Excluir</button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  </div>
)

const ClientDashboard = ({ handleLogout, authEmail }) => (
  <div className="auth-page">
    <div className="auth-card" style={{ maxWidth: 520 }}>
      <h2>Área do cliente</h2>
      <p style={{ textAlign: 'center', marginBottom: 16 }}>Conectado como <strong>{authEmail}</strong></p>
      <p className="login-hint" style={{ textAlign: 'center' }}>Catálogo integrado à API virá nas próximas tarefas.</p>
      <button type="button" className="btn-submit" onClick={handleLogout}>Sair</button>
    </div>
  </div>
)

const EmployeeDashboard = ({ handleLogout, authEmail }) => (
  <div className="auth-page">
    <div className="auth-card" style={{ maxWidth: 520 }}>
      <h2>Área do funcionário</h2>
      <p style={{ textAlign: 'center', marginBottom: 16 }}>Conectado como <strong>{authEmail}</strong></p>
      <p className="login-hint" style={{ textAlign: 'center' }}>Painel operacional pode ser integrado à API aqui.</p>
      <button type="button" className="btn-submit" onClick={handleLogout}>Sair</button>
    </div>
  </div>
)

function App() {
  const [currentPage, setCurrentPage] = useState(initialPageFromStored)
  const [authToken, setAuthToken] = useState(() => initialAuthFromStored().token)
  const [authEmail, setAuthEmail] = useState(() => initialAuthFromStored().email)
  const [authRole, setAuthRole] = useState(() => initialAuthFromStored().role)
  const [parts, setParts] = useState([])
  const [categories, setCategories] = useState([])
  const [cars, setCars] = useState([])
  const [carBrands, setCarBrands] = useState([])
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState('')
  const [selectedBrandFilter, setSelectedBrandFilter] = useState('')
  const [selectedCarFilter, setSelectedCarFilter] = useState('')
  const [employees, setEmployees] = useState([])
  const [clients, setClients] = useState([])
  const [loginEmail, setLoginEmail] = useState('')
  const [loginPassword, setLoginPassword] = useState('')
  const [loginError, setLoginError] = useState('')
  const [loginSubmitting, setLoginSubmitting] = useState(false)
  const [newPartForm, setNewPartForm] = useState({ name: '', description: '', price: '', categoryIds: [], carIds: [] })
  const [newEmployeeForm, setNewEmployeeForm] = useState({ nome: '', cargo: '', email: '', telefone: '' })
  const [newClientForm, setNewClientForm] = useState({ nome: '', email: '', telefone: '', endereco: '' })
  const [newCategoryForm, setNewCategoryForm] = useState({ name: '' })
  const [newBrandForm, setNewBrandForm] = useState({ name: '' })
  const [newCarForm, setNewCarForm] = useState({ nome: '', ano: '', carBrandId: '' })
  const [registerForm, setRegisterForm] = useState({ nome: '', sobrenome: '', cpf: '', email: '', celular: '', endereco: '', cidade: '', bairro: '', rua: '', numero: '', complemento: '' })
  const [editingEmployee, setEditingEmployee] = useState(null)
  const [editingClient, setEditingClient] = useState(null)
  const [editingPart, setEditingPart] = useState(null)
  const [editingCategory, setEditingCategory] = useState(null)
  const [editingBrand, setEditingBrand] = useState(null)
  const [editingCar, setEditingCar] = useState(null)

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const [productRes, categoryRes, carRes, brandRes] = await Promise.all([
          apiFetch('/products', { method: 'GET', auth: false }),
          apiFetch('/categories', { method: 'GET', auth: false }),
          apiFetch('/cars', { method: 'GET', auth: false }),
          apiFetch('/car-brands', { method: 'GET', auth: false }),
        ])

        if (productRes.ok) {
          setParts(await productRes.json())
        }
        if (categoryRes.ok) {
          setCategories(await categoryRes.json())
        }
        if (carRes.ok) {
          setCars(await carRes.json())
        }
        if (brandRes.ok) {
          setCarBrands(await brandRes.json())
        }
      } catch (error) {
        console.warn('Falha ao carregar dados iniciais:', error)
      }
    }

    loadInitialData()
  }, [])

  setAccessTokenGetter(() => authToken)

  const filteredParts = parts.filter(part => {
    const query = searchQuery.trim().toLowerCase()
    if (query && !(`${part.name || ''} ${part.description || ''}`.toLowerCase().includes(query))) {
      return false
    }

    if (selectedCategoryFilter) {
      if (!Array.isArray(part.categoryIds) || !part.categoryIds.some(id => String(id) === selectedCategoryFilter)) {
        return false
      }
    }

    if (selectedBrandFilter) {
      const matchesBrand = (part.carIds || []).some(id => {
        const car = cars.find(carItem => carItem.id === id)
        return car?.carBrandName === selectedBrandFilter
      })
      if (!matchesBrand) {
        return false
      }
    }

    if (selectedCarFilter) {
      if (!Array.isArray(part.carIds) || !part.carIds.some(id => String(id) === selectedCarFilter)) {
        return false
      }
    }

    return true
  })

  const refreshCategories = async () => {
    try {
      const res = await apiFetch('/categories', { method: 'GET', auth: false })
      if (res.ok) {
        setCategories(await res.json())
      }
    } catch (error) {
      console.warn('Falha ao atualizar categorias:', error)
    }
  }

  const refreshCars = async () => {
    try {
      const res = await apiFetch('/cars', { method: 'GET', auth: false })
      if (res.ok) {
        setCars(await res.json())
      }
    } catch (error) {
      console.warn('Falha ao atualizar veículos:', error)
    }
  }

  const refreshBrands = async () => {
    try {
      const res = await apiFetch('/car-brands', { method: 'GET', auth: false })
      if (res.ok) {
        setCarBrands(await res.json())
      }
    } catch (error) {
      console.warn('Falha ao atualizar marcas:', error)
    }
  }

  const handleCreateCategory = async (e) => {
    e.preventDefault()
    if (!newCategoryForm.name.trim()) {
      alert('Informe o nome da categoria.')
      return
    }

    try {
      const res = await apiFetch(editingCategory ? `/categories/${editingCategory.id}` : '/categories', {
        method: editingCategory ? 'PUT' : 'POST',
        body: { name: newCategoryForm.name.trim() },
      })
      if (!res.ok) {
        throw new Error(await res.text())
      }
      await refreshCategories()
      setNewCategoryForm({ name: '' })
      setEditingCategory(null)
      alert(editingCategory ? 'Categoria atualizada!' : 'Categoria criada!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleEditCategory = (category) => {
    setNewCategoryForm({ name: category.name })
    setEditingCategory(category)
  }

  const handleDeleteCategory = async (id) => {
    if (!window.confirm('Deseja excluir esta categoria?')) return
    try {
      const res = await apiFetch(`/categories/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error(await res.text())
      await refreshCategories()
      alert('Categoria removida!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleCreateBrand = async (e) => {
    e.preventDefault()
    if (!newBrandForm.name.trim()) {
      alert('Informe o nome da marca.')
      return
    }

    try {
      const res = await apiFetch(editingBrand ? `/car-brands/${editingBrand.id}` : '/car-brands', {
        method: editingBrand ? 'PUT' : 'POST',
        body: { name: newBrandForm.name.trim() },
      })
      if (!res.ok) {
        throw new Error(await res.text())
      }
      await refreshBrands()
      setNewBrandForm({ name: '' })
      setEditingBrand(null)
      alert(editingBrand ? 'Marca atualizada!' : 'Marca criada!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleEditBrand = (brand) => {
    setNewBrandForm({ name: brand.name })
    setEditingBrand(brand)
  }

  const handleDeleteBrand = async (id) => {
    if (!window.confirm('Deseja excluir esta marca?')) return
    try {
      const res = await apiFetch(`/car-brands/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error(await res.text())
      await refreshBrands()
      alert('Marca removida!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleCreateCar = async (e) => {
    e.preventDefault()
    if (!newCarForm.nome.trim() || !newCarForm.ano || !newCarForm.carBrandId) {
      alert('Informe nome, ano e marca do veículo.')
      return
    }

    try {
      const body = {
        nome: newCarForm.nome.trim(),
        ano: Number(newCarForm.ano),
        carBrandId: Number(newCarForm.carBrandId),
      }
      const res = await apiFetch(`/cars${editingCar ? `/${editingCar.id}` : ''}`, {
        method: editingCar ? 'PUT' : 'POST',
        body,
      })
      if (!res.ok) throw new Error(await res.text())
      await refreshCars()
      setNewCarForm({ nome: '', ano: '', carBrandId: '' })
      setEditingCar(null)
      alert(editingCar ? 'Veículo atualizado!' : 'Veículo criado!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleEditCar = (car) => {
    setNewCarForm({ nome: car.nome, ano: car.ano ?? '', carBrandId: String(car.carBrandId) })
    setEditingCar(car)
  }

  const handleDeleteCar = async (id) => {
    if (!window.confirm('Deseja excluir este veículo?')) return
    try {
      const res = await apiFetch(`/cars/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error(await res.text())
      await refreshCars()
      alert('Veículo removido!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleCancelCategory = () => {
    setNewCategoryForm({ name: '' })
    setEditingCategory(null)
  }

  const handleCancelBrand = () => {
    setNewBrandForm({ name: '' })
    setEditingBrand(null)
  }

  const handleCancelCar = () => {
    setNewCarForm({ nome: '', ano: '', carBrandId: '' })
    setEditingCar(null)
  }

  const handleLogin = async (email, password) => {
    setLoginError('')

    if (!email || !password) {
      setLoginError('Email e senha são obrigatórios')
      return
    }

    setLoginSubmitting(true)
    try {
      const res = await apiFetch('/auth/login', {
        method: 'POST',
        body: { email, password },
        auth: false,
      })

      const contentType = res.headers.get('content-type') ?? ''
      let data = null
      if (contentType.includes('application/json')) {
        data = await res.json()
      } else {
        const text = await res.text()
        if (!res.ok) {
          setLoginError(text.trim() || 'Email ou senha incorretos')
          return
        }
      }

      if (!res.ok) {
        setLoginError(
          typeof data === 'object' && data?.message
            ? data.message
            : 'Email ou senha incorretos',
        )
        return
      }

      if (!data?.token) {
        setLoginError('Resposta inválida da API (sem token).')
        return
      }

      const role = data.role ?? null
      setAuthToken(data.token)
      setAuthEmail(data.email ?? email)
      setAuthRole(role)
      writeStoredAuth({ token: data.token, email: data.email ?? email, role })

      setLoginEmail('')
      setLoginPassword('')

      const r = String(role ?? '').toUpperCase()
      if (r === 'GERENTE') setCurrentPage('admin-dashboard')
      else if (r === 'FUNCIONARIO') setCurrentPage('employee-dashboard')
      else setCurrentPage('client-dashboard')
    } catch {
      setLoginError('Não foi possível conectar à API. Confira VITE_API_BASE_URL e se o Spring Boot está rodando.')
    } finally {
      setLoginSubmitting(false)
    }
  }

  const handleLogout = async () => {
    if (authToken) {
      try {
        await apiFetch('/auth/logout', { method: 'POST' })
      } catch {
        /* falha de rede: ainda encerramos a sessão no cliente */
      }
    }
    setAuthToken(null)
    setAuthEmail(null)
    setAuthRole(null)
    clearStoredAuth()
    setCurrentPage('home')
  }

  const isLoggedIn = Boolean(authToken)

  const refreshProducts = async () => {
    try {
      const res = await apiFetch('/products', { method: 'GET', auth: false })
      if (res.ok) {
        setParts(await res.json())
      }
    } catch (error) {
      console.warn('Falha ao atualizar produtos:', error)
    }
  }

  const handleCreatePart = async () => {
    if (!newPartForm.name || !newPartForm.description || !newPartForm.price || newPartForm.categoryIds.length === 0 || newPartForm.carIds.length === 0) {
      alert('Preencha todos os campos obrigatórios e selecione ao menos uma categoria e um veículo compatível.')
      return
    }

    try {
      const requestBody = {
        name: newPartForm.name,
        description: newPartForm.description,
        price: Number(newPartForm.price),
        categoryIds: newPartForm.categoryIds,
        carIds: newPartForm.carIds,
      }

      if (editingPart) {
        const res = await apiFetch(`/products/${editingPart.id}`, { method: 'PUT', body: requestBody })
        if (!res.ok) {
          const errorBody = await res.text()
          throw new Error(errorBody || 'Falha ao atualizar a peça.')
        }
        alert('Peça atualizada com sucesso!')
      } else {
        const res = await apiFetch('/products', { method: 'POST', body: requestBody })
        if (!res.ok) {
          const errorBody = await res.text()
          throw new Error(errorBody || 'Falha ao criar a peça.')
        }
        alert('Peça criada com sucesso!')
      }

      await refreshProducts()
      setNewPartForm({ name: '', description: '', price: '', categoryIds: [], carIds: [] })
      setEditingPart(null)
    } catch (error) {
      alert(error.message)
    }
  }

  const handleEditPart = (part) => {
    setNewPartForm({
      name: part.name || '',
      description: part.description || '',
      price: part.price ?? '',
      categoryIds: part.categoryIds || [],
      carIds: part.carIds || [],
    })
    setEditingPart(part)
  }

  const handleDeletePart = async (id) => {
    if (!window.confirm('Tem certeza que deseja excluir esta peça?')) {
      return
    }

    try {
      const res = await apiFetch(`/products/${id}`, { method: 'DELETE' })
      if (!res.ok) {
        const errorBody = await res.text()
        throw new Error(errorBody || 'Falha ao excluir a peça.')
      }
      await refreshProducts()
      alert('Peça excluída com sucesso!')
    } catch (error) {
      alert(error.message)
    }
  }

  const handleCancelEditPart = () => {
    setNewPartForm({ name: '', description: '', price: '', categoryIds: [], carIds: [] })
    setEditingPart(null)
  }

  const handleCreateEmployee = (e) => {
    e.preventDefault()
    if (newEmployeeForm.nome && newEmployeeForm.cargo && newEmployeeForm.email) {
      if (editingEmployee) {
        // Editando funcionário existente
        setEmployees(employees.map(emp => 
          emp.id === editingEmployee.id ? { ...newEmployeeForm, id: editingEmployee.id } : emp
        ))
        alert('Funcionário atualizado com sucesso!')
      } else {
        // Criando novo funcionário
        setEmployees([...employees, { ...newEmployeeForm, id: Date.now() }])
        alert('Funcionário cadastrado com sucesso!')
      }
      setNewEmployeeForm({ nome: '', cargo: '', email: '', telefone: '' })
      setEditingEmployee(null)
    } else {
      alert('Preencha todos os campos obrigatórios.')
    }
  }

  const handleEditEmployee = (employee) => {
    setNewEmployeeForm({ ...employee })
    setEditingEmployee(employee)
  }

  const handleDeleteEmployee = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este funcionário?')) {
      setEmployees(employees.filter(emp => emp.id !== id))
      alert('Funcionário excluído com sucesso!')
    }
  }

  const handleCancelEditEmployee = () => {
    setNewEmployeeForm({ nome: '', cargo: '', email: '', telefone: '' })
    setEditingEmployee(null)
  }

  const handleCreateClient = (e) => {
    e.preventDefault()
    if (newClientForm.nome && newClientForm.email) {
      if (editingClient) {
        // Editando cliente existente
        setClients(clients.map(client => 
          client.id === editingClient.id ? { ...newClientForm, id: editingClient.id } : client
        ))
        alert('Cliente atualizado com sucesso!')
      } else {
        // Criando novo cliente
        setClients([...clients, { ...newClientForm, id: Date.now() }])
        alert('Cliente cadastrado com sucesso!')
      }
      setNewClientForm({ nome: '', email: '', telefone: '', endereco: '' })
      setEditingClient(null)
    } else {
      alert('Preencha todos os campos obrigatórios.')
    }
  }

  const handleEditClient = (client) => {
    setNewClientForm({ ...client })
    setEditingClient(client)
  }

  const handleDeleteClient = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      setClients(clients.filter(client => client.id !== id))
      alert('Cliente excluído com sucesso!')
    }
  }

  const handleCancelEditClient = () => {
    setNewClientForm({ nome: '', email: '', telefone: '', endereco: '' })
    setEditingClient(null)
  }

  const handleRegister = (e) => {
    e.preventDefault()
    if (registerForm.nome && registerForm.sobrenome && registerForm.email && registerForm.celular) {
      alert('Cliente registrado com sucesso!')
      setRegisterForm({ nome: '', sobrenome: '', cpf: '', email: '', celular: '', endereco: '', cidade: '', bairro: '', rua: '', numero: '', complemento: '' })
      setCurrentPage('home')
    } else {
      alert('Preencha todos os campos obrigatórios.')
    }
  }

  if (!isLoggedIn) {
    if (currentPage === 'login') {
      return (
        <LoginPage
          setCurrentPage={setCurrentPage}
          loginEmail={loginEmail}
          setLoginEmail={setLoginEmail}
          loginPassword={loginPassword}
          setLoginPassword={setLoginPassword}
          loginError={loginError}
          loginSubmitting={loginSubmitting}
          handleLogin={handleLogin}
        />
      )
    }
    if (currentPage === 'register') return <RegisterPage setCurrentPage={setCurrentPage} registerForm={registerForm} setRegisterForm={setRegisterForm} handleRegister={handleRegister} />
    return <HomePage setCurrentPage={setCurrentPage} parts={parts} categories={categories} cars={cars} />
  }

  const roleUpper = String(authRole ?? '').toUpperCase()

  if (roleUpper === 'CLIENTE') {
    return <ClientDashboard handleLogout={handleLogout} authEmail={authEmail} />
  }

  if (roleUpper === 'FUNCIONARIO') {
    return <EmployeeDashboard handleLogout={handleLogout} authEmail={authEmail} />
  }

  if (roleUpper === 'GERENTE') {
    if (currentPage === 'admin-employees') return <AdminEmployees setCurrentPage={setCurrentPage} handleLogout={handleLogout} employees={employees} newEmployeeForm={newEmployeeForm} setNewEmployeeForm={setNewEmployeeForm} handleCreateEmployee={handleCreateEmployee} editingEmployee={editingEmployee} handleEditEmployee={handleEditEmployee} handleDeleteEmployee={handleDeleteEmployee} handleCancelEditEmployee={handleCancelEditEmployee} />
    if (currentPage === 'admin-clients') return <AdminClients setCurrentPage={setCurrentPage} handleLogout={handleLogout} clients={clients} newClientForm={newClientForm} setNewClientForm={setNewClientForm} handleCreateClient={handleCreateClient} editingClient={editingClient} handleEditClient={handleEditClient} handleDeleteClient={handleDeleteClient} handleCancelEditClient={handleCancelEditClient} />
    if (currentPage === 'admin-categories') return <AdminCategories setCurrentPage={setCurrentPage} handleLogout={handleLogout} categories={categories} newCategoryForm={newCategoryForm} setNewCategoryForm={setNewCategoryForm} handleCreateCategory={handleCreateCategory} editingCategory={editingCategory} handleEditCategory={handleEditCategory} handleDeleteCategory={handleDeleteCategory} handleCancelCategory={handleCancelCategory} />
    if (currentPage === 'admin-brands') return <AdminBrands setCurrentPage={setCurrentPage} handleLogout={handleLogout} carBrands={carBrands} newBrandForm={newBrandForm} setNewBrandForm={setNewBrandForm} handleCreateBrand={handleCreateBrand} editingBrand={editingBrand} handleEditBrand={handleEditBrand} handleDeleteBrand={handleDeleteBrand} handleCancelBrand={handleCancelBrand} />
    if (currentPage === 'admin-cars') return <AdminCars setCurrentPage={setCurrentPage} handleLogout={handleLogout} cars={cars} carBrands={carBrands} newCarForm={newCarForm} setNewCarForm={setNewCarForm} handleCreateCar={handleCreateCar} editingCar={editingCar} handleEditCar={handleEditCar} handleDeleteCar={handleDeleteCar} handleCancelCar={handleCancelCar} />
    return <AdminDashboard
      setCurrentPage={setCurrentPage}
      handleLogout={handleLogout}
      parts={filteredParts}
      newPartForm={newPartForm}
      setNewPartForm={setNewPartForm}
      handleCreatePart={handleCreatePart}
      clients={clients}
      editingPart={editingPart}
      handleEditPart={handleEditPart}
      handleDeletePart={handleDeletePart}
      handleCancelEditPart={handleCancelEditPart}
      categories={categories}
      cars={cars}
      searchQuery={searchQuery}
      setSearchQuery={setSearchQuery}
      selectedCategoryFilter={selectedCategoryFilter}
      setSelectedCategoryFilter={setSelectedCategoryFilter}
      selectedBrandFilter={selectedBrandFilter}
      setSelectedBrandFilter={setSelectedBrandFilter}
      selectedCarFilter={selectedCarFilter}
      setSelectedCarFilter={setSelectedCarFilter}
    />
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>Sessão</h2>
        <p className="login-hint">Papel não reconhecido: {authRole ?? '(vazio)'}</p>
        <button type="button" className="btn-submit" onClick={handleLogout}>Sair</button>
      </div>
    </div>
  )
}

export default App
