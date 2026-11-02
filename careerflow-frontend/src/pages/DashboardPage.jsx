import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Briefcase, CalendarCheck, Award, XCircle, ArrowUpRight, TrendingUp } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';

export default function DashboardPage() {
  const [stats, setStats] = useState(null);
  const [recentApps, setRecentApps] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const statsRes = await api.get('/applications/dashboard/stats');
        setStats(statsRes.data);

        const appsRes = await api.get('/applications?page=0&size=5&sortBy=appliedDate&sortDir=desc');
        setRecentApps(appsRes.data.content);
      } catch (err) {
        console.error('Failed to load dashboard data', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64 text-slate-400 text-sm">
        Loading analytics...
      </div>
    );
  }

  const statCards = [
    { label: 'Total Applications', value: stats?.totalApplications || 0, icon: Briefcase, color: 'text-teal-400', bg: 'bg-teal-500/10' },
    { label: 'Active Interviews', value: stats?.activeInterviews || 0, icon: CalendarCheck, color: 'text-amber-400', bg: 'bg-amber-500/10' },
    { label: 'Offers Received', value: stats?.offersReceived || 0, icon: Award, color: 'text-emerald-400', bg: 'bg-emerald-500/10' },
    { label: 'Rejections', value: stats?.rejectedApplications || 0, icon: XCircle, color: 'text-rose-400', bg: 'bg-rose-500/10' },
  ];

  const chartData = stats?.statusCounts
    ? Object.entries(stats.statusCounts).map(([status, count]) => ({ status, count }))
    : [];

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
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-white">Application Pipeline & Metrics</h1>
        <p className="text-sm text-slate-400 mt-1">Live metrics from your Spring Boot REST backend</p>
      </div>

      {/* Metric Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {statCards.map((card) => {
          const Icon = card.icon;
          return (
            <div key={card.label} className="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 backdrop-blur-sm">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-medium text-slate-400">{card.label}</span>
                <div className={`p-2 rounded-lg ${card.bg}`}>
                  <Icon className={`h-4 w-4 ${card.color}`} />
                </div>
              </div>
              <div className="text-2xl font-bold text-white tracking-tight">{card.value}</div>
            </div>
          );
        })}
      </div>

      {/* Analytics & Funnel Chart */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-slate-900/60 border border-slate-800/80 rounded-xl p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-base font-semibold text-white">Application Funnel Breakdown</h2>
              <p className="text-xs text-slate-400 mt-0.5">Distribution across interview stages</p>
            </div>
            <TrendingUp className="h-4 w-4 text-teal-400" />
          </div>

          {chartData.length > 0 ? (
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                  <XAxis dataKey="status" stroke="#64748b" fontSize={11} tickLine={false} />
                  <YAxis stroke="#64748b" fontSize={11} tickLine={false} allowDecimals={false} />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '8px', color: '#fff' }}
                    itemStyle={{ color: '#14b8a6' }}
                  />
                  <Bar dataKey="count" radius={[6, 6, 0, 0]} fill="#14b8a6" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          ) : (
            <div className="h-64 flex items-center justify-center text-sm text-slate-500">
              No application status data available yet. Start tracking applications!
            </div>
          )}
        </div>

        {/* Quick Tips / Backend Architecture Card */}
        <div className="bg-gradient-to-br from-slate-900/90 to-teal-950/20 border border-slate-800/80 rounded-xl p-6 flex flex-col justify-between">
          <div>
            <span className="text-[11px] font-semibold tracking-wider text-teal-400 uppercase">Architecture Highlights</span>
            <h3 className="text-lg font-bold text-white mt-1">Enterprise Spring Boot 3</h3>
            <p className="text-xs text-slate-400 mt-2 leading-relaxed">
              Every card and chart on this dashboard executes Spring Data JPA Specification aggregations via HikariCP on your MySQL 8 instance.
            </p>
            <ul className="text-xs text-slate-300 space-y-2 mt-4">
              <li className="flex items-center gap-2">
                <span className="h-1.5 w-1.5 rounded-full bg-teal-400"></span>
                <span>Stateless JWT Bearer Auth</span>
              </li>
              <li className="flex items-center gap-2">
                <span className="h-1.5 w-1.5 rounded-full bg-teal-400"></span>
                <span>Indexed MySQL Foreign Keys</span>
              </li>
              <li className="flex items-center gap-2">
                <span className="h-1.5 w-1.5 rounded-full bg-teal-400"></span>
                <span>REST Response Envelopes</span>
              </li>
            </ul>
          </div>
          <div className="pt-4 border-t border-slate-800/80 mt-4">
            <a
              href={`${(import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1').replace(/\/api\/v1\/?$/, '')}/swagger-ui/index.html`}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1.5 text-xs text-teal-400 font-semibold hover:underline"
            >
              Explore Swagger / OpenAPI docs <ArrowUpRight className="h-3 w-3" />
            </a>
          </div>
        </div>
      </div>

      {/* Recent Applications Table */}
      <div className="bg-slate-900/60 border border-slate-800/80 rounded-xl p-6">
        <h2 className="text-base font-semibold text-white mb-4">Recent Applications</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-xs uppercase text-slate-400 border-b border-slate-800">
              <tr>
                <th className="pb-3 font-medium">Job Title</th>
                <th className="pb-3 font-medium">Company</th>
                <th className="pb-3 font-medium">Status</th>
                <th className="pb-3 font-medium">Applied Date</th>
                <th className="pb-3 font-medium">Interviews</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/50">
              {recentApps.length > 0 ? (
                recentApps.map((app) => (
                  <tr key={app.id} className="hover:bg-slate-800/20 transition-colors">
                    <td className="py-3.5 font-medium text-white">{app.jobTitle}</td>
                    <td className="py-3.5 text-slate-300">{app.company?.name}</td>
                    <td className="py-3.5">
                      <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStatusBadge(app.status)}`}>
                        {app.status}
                      </span>
                    </td>
                    <td className="py-3.5 text-slate-400 text-xs">{app.appliedDate}</td>
                    <td className="py-3.5 text-slate-300 text-xs">{app.interviewRoundsCount} scheduled</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="py-6 text-center text-slate-500 text-sm">
                    No applications tracked yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
