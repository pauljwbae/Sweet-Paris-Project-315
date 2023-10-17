#product usage chart
-- Product Usage Chart:  Given a time window, display a chart (table, graph, diagram) that depicts the amount of inventory used during that time period.
-- assumes we have a usage_quantity column that is the amount that that inventory item was used  

select date(orderdatetime) as date and sum(usage_quantity) as total_usage from inventory where orderdatetime between 'start_date' and 'end_date'
group by date(orderdatetime)
order by date(orderdatetime)
