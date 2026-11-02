import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Plus, Search, Filter, ExternalLink, Calendar, Trash2, Edit3 } from 'lucide-react';

export default function ApplicationsPage() {
  const [applications, setApplications] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create'); // 'create' or 'edit'
  const [currentId, setCurrentId] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    jobTitle: '',
    jobUrl: '',
    jobLocation: '',
    salaryMin: '',
    salaryMax: '',
    status: 'APPLIED',
    appliedDate: new Date().toISOString().split('T')[0],
    notes: '',
    skillsText: '',
  });

  const fetchApplications = async () => {
    setLoading(true);
    try {
      let query = `/applications?page=${page}&size=10&sortBy=appliedDate&sortDir=desc`;
      if (search.trim()) query += `&search=${encodeURIComponent(search.trim())}`;
      if (statusFilter) query += `&status=${statusFilter}`;

      const res = await api.get(query);
      setApplications(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error('Failed to load applications', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCompanies = async () => {
    try {
      const res = await api.get('/companies?page=0&size=100');
      setCompanies(res.data.content);
    } catch (err) {
      console.error('Failed to load companies', err);
    }
  };

  useEffect(() => {
    fetchApplications();
  }, [page, statusFilter]);

  useEffect(() => {
    fetchCompanies();
  }, []);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchApplications();
  };

  const openCreateModal = () => {
    setModalMode('create');
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      jobTitle: '',
      jobUrl: '',
      jobLocation: '',
      salaryMin: '',
      salaryMax: '',
      status: 'APPLIED',
      appliedDate: new Date().toISOString().split('T')[0],
      notes: '',
      skillsText: '',
    });
    setIsModalOpen(true);
  };

  const openEditModal = (app) => {
    setModalMode('edit');
    setCurrentId(app.id);
    setFormData({
      companyId: app.company?.id || '',
      jobTitle: app.jobTitle,
      jobUrl: app.jobUrl || '',
      jobLocation: app.jobLocation || '',
      salaryMin: app.salaryMin || '',
      salaryMax: app.salaryMax || '',
      status: app.status,
      appliedDate: app.appliedDate,
      notes: app.notes || '',
      skillsText: app.skills ? Array.from(app.skills).join(', ') : '',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        companyId: Number(formData.companyId),
        jobTitle: formData.jobTitle,
        jobUrl: formData.jobUrl,
        jobLocation: formData.jobLocation,
        salaryMin: formData.salaryMin ? Number(formData.salaryMin) : null,
        salaryMax: formData.salaryMax ? Number(formData.salaryMax) : null,
        status: formData.status,
        appliedDate: formData.appliedDate,
        notes: formData.notes,
        skills: formData.skillsText
          ? formData.skillsText.split(',').map((s) => s.trim()).filter(Boolean)
          : [],
      };

      if (modalMode === 'create') {
        await api.post('/applications', payload);
      } else {
        await api.put(`/applications/${currentId}`, payload);
      }
      setIsModalOpen(false);
      fetchApplications();
    } catch (err) {
      alert(err.message || 'Error saving application');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this application?')) {
      try {
        await api.delete(`/applications/${id}`);
        fetchApplications();
      } catch (err) {
        alert(err.message || 'Error deleting application');
      }
    }
  };

  const handleQuickStatusChange = async (id, newStatus) => {
    try {
      await api.patch(`/applications/${id}/status`, { status: newStatus });
      fetchApplications();
    } catch (err) {
      alert(err.message || 'Error updating status');
    }
  };

  const getStatusBadge = (status) => {
    const map = {
      APPLIED: 'bg-slate-800 text-slate-300 border-slate-700',
      SCREENING: 'bg-sky-500/10 text-sky-400 border-sky-500/20',
      TECHNICAL: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
      HR: 'bg-purple-500/10 text-purple-400 border-purple-500/20',
      OFFER: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
      REJECTED: 'bg-rose-500/10 text-rose-400 border-rose-500/20',
      WITHDRAWN: 'bg-slate-700 text-slate-400 border-slate-600',
    };
    return map[status] || 'bg-slate-800 text-slate-300 border-slate-700';
  };

  return (
    <div className="space-y-6">
      {/* Top Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Job Applications</h1>
          <p className="text-sm text-slate-400">Track and manage every placement interview pipeline</p>
        </div>
        <button
          onClick={openCreateModal}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-lg bg-teal-500 hover:bg-teal-400 text-slate-950 font-semibold text-sm transition-all shadow-lg shadow-teal-500/20"
        >
          <Plus className="h-4 w-4" /> Add Application
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row gap-3">
        <form onSubmit={handleSearchSubmit} className="flex-1 relative">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by job title, company name, or location..."
            className="w-full pl-10 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-lg text-sm text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-teal-500/50"
          />
        </form>

        <select
          value={statusFilter}
          onChange={(e) => {
            setStatusFilter(e.target.value);
            setPage(0);
          }}
          className="bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/50"
        >
          <option value="">All Statuses</option>
          <option value="APPLIED">Applied</option>
          <option value="SCREENING">Screening</option>
          <option value="TECHNICAL">Technical Round</option>
          <option value="HR">HR Round</option>
          <option value="OFFER">Offer</option>
          <option value="REJECTED">Rejected</option>
          <option value="WITHDRAWN">Withdrawn</option>
        </select>
      </div>

      {/* Applications Table */}
      <div className="bg-slate-900/60 border border-slate-800/80 rounded-xl overflow-hidden shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-xs uppercase text-slate-400 bg-slate-900/90 border-b border-slate-800">
              <tr>
                <th className="px-5 py-3.5 font-medium">Job Title & Company</th>
                <th className="px-5 py-3.5 font-medium">Location</th>
                <th className="px-5 py-3.5 font-medium">Status Transition</th>
                <th className="px-5 py-3.5 font-medium">Applied Date</th>
                <th className="px-5 py-3.5 font-medium">Skills</th>
                <th className="px-5 py-3.5 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/50">
              {loading ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-slate-400">
                    Loading applications...
                  </td>
                </tr>
              ) : applications.length > 0 ? (
                applications.map((app) => (
                  <tr key={app.id} className="hover:bg-slate-800/25 transition-colors">
                    <td className="px-5 py-4">
                      <div className="font-semibold text-white flex items-center gap-1.5">
                        {app.jobTitle}
                        {app.jobUrl && (
                          <a href={app.jobUrl} target="_blank" rel="noopener noreferrer" className="text-slate-500 hover:text-teal-400">
                            <ExternalLink className="h-3 w-3" />
                          </a>
                        )}
                      </div>
                      <div className="text-xs text-slate-400 mt-0.5">{app.company?.name}</div>
                    </td>
                    <td className="px-5 py-4 text-slate-300 text-xs">
                      {app.jobLocation || 'Remote'}
                    </td>
                    <td className="px-5 py-4">
                      <select
                        value={app.status}
                        onChange={(e) => handleQuickStatusChange(app.id, e.target.value)}
                        className={`text-xs font-semibold px-2.5 py-1 rounded-full border bg-slate-900 cursor-pointer ${getStatusBadge(app.status)}`}
                      >
                        <option value="APPLIED">APPLIED</option>
                        <option value="SCREENING">SCREENING</option>
                        <option value="TECHNICAL">TECHNICAL</option>
                        <option value="HR">HR</option>
                        <option value="OFFER">OFFER</option>
                        <option value="REJECTED">REJECTED</option>
                        <option value="WITHDRAWN">WITHDRAWN</option>
                      </select>
                    </td>
                    <td className="px-5 py-4 text-slate-400 text-xs">
                      {app.appliedDate}
                    </td>
                    <td className="px-5 py-4">
                      <div className="flex flex-wrap gap-1 max-w-xs">
                        {app.skills && Array.from(app.skills).map((skill) => (
                          <span key={skill} className="px-1.5 py-0.5 rounded bg-slate-800 text-[11px] text-teal-300 border border-slate-700">
                            {skill}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="px-5 py-4 text-right">
                      <div className="inline-flex items-center gap-2">
                        <button
                          onClick={() => openEditModal(app)}
                          className="p-1.5 text-slate-400 hover:text-teal-400 hover:bg-slate-800 rounded transition-colors"
                          title="Edit application"
                        >
                          <Edit3 className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(app.id)}
                          className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded transition-colors"
                          title="Delete application"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-slate-500">
                    No applications found matching your criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination controls */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-5 py-3 border-t border-slate-800 bg-slate-900/50">
            <span className="text-xs text-slate-400">
              Page {page + 1} of {totalPages}
            </span>
            <div className="flex gap-2">
              <button
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                className="px-3 py-1 rounded bg-slate-800 text-xs text-slate-300 disabled:opacity-40"
              >
                Previous
              </button>
              <button
                disabled={page + 1 >= totalPages}
                onClick={() => setPage(page + 1)}
                className="px-3 py-1 rounded bg-slate-800 text-xs text-slate-300 disabled:opacity-40"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Create / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg p-6 shadow-2xl space-y-4">
            <h2 className="text-lg font-bold text-white">
              {modalMode === 'create' ? 'Create Job Application' : 'Edit Application'}
            </h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Company</label>
                  <select
                    required
                    value={formData.companyId}
                    onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                  >
                    <option value="" disabled>Select Company</option>
                    {companies.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Job Title</label>
                  <input
                    type="text"
                    required
                    value={formData.jobTitle}
                    onChange={(e) => setFormData({ ...formData, jobTitle: e.target.value })}
                    placeholder="e.g. SDE-1 Backend"
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Location</label>
                  <input
                    type="text"
                    value={formData.jobLocation}
                    onChange={(e) => setFormData({ ...formData, jobLocation: e.target.value })}
                    placeholder="Remote / City"
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Status</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                  >
                    <option value="APPLIED">Applied</option>
                    <option value="SCREENING">Screening</option>
                    <option value="TECHNICAL">Technical Round</option>
                    <option value="HR">HR Round</option>
                    <option value="OFFER">Offer</option>
                    <option value="REJECTED">Rejected</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Min Salary ($)</label>
                  <input
                    type="number"
                    value={formData.salaryMin}
                    onChange={(e) => setFormData({ ...formData, salaryMin: e.target.value })}
                    placeholder="e.g. 90000"
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Max Salary ($)</label>
                  <input
                    type="number"
                    value={formData.salaryMax}
                    onChange={(e) => setFormData({ ...formData, salaryMax: e.target.value })}
                    placeholder="e.g. 130000"
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Required Skills (comma-separated)</label>
                <input
                  type="text"
                  value={formData.skillsText}
                  onChange={(e) => setFormData({ ...formData, skillsText: e.target.value })}
                  placeholder="Java, Spring Boot, MySQL, Docker"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Notes / Referral details</label>
                <textarea
                  rows={2}
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  placeholder="Referral name, job posting link, or notes..."
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-teal-500/50"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-sm font-medium text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm font-semibold text-slate-950 bg-teal-500 hover:bg-teal-400 rounded-lg shadow-lg shadow-teal-500/20"
                >
                  Save Application
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
